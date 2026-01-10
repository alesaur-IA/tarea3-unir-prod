package com.unir.biblioteca.buscador.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.biblioteca.buscador.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BuscadorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${elasticsearch.url}")
    private String esUrl;

    @Value("${elasticsearch.index}")
    private String index;

    public BuscadorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1) Buscar real en Elasticsearch
    public BusquedaResponseDTO buscar(String q, Integer page, Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        int from = p * s;

        String url = esUrl + "/" + index + "/_search";

        String queryJson;
        if (q == null || q.isBlank()) {
            queryJson = """
            {
              "from": %d,
              "size": %d,
              "query": { "match_all": {} }
            }
            """.formatted(from, s);
        } else {
            // Multi-match simple (titulo/autor/categoria/sinopsis)
            queryJson = """
            {
              "from": %d,
              "size": %d,
              "query": {
                "multi_match": {
                  "query": "%s",
                  "fields": ["titulo^3", "autor^2", "categoria^2", "sinopsis"]
                }
              }
            }
            """.formatted(from, s, escape(q));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(queryJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode hits = root.path("hits");
            long total = hits.path("total").path("value").asLong(0);

            List<LibroDTO> content = new ArrayList<>();
            for (JsonNode h : hits.path("hits")) {
                JsonNode src = h.path("_source");

                // OJO: si tu JSON trae "id" como número, aquí lo convertimos a String
                String id = src.path("id").isMissingNode() ? null : src.path("id").asText();

                LibroDTO libro = new LibroDTO(
                        id,
                        textOrNull(src, "titulo"),
                        textOrNull(src, "autor"),
                        src.path("anio_publicacion").isMissingNode() || src.path("anio_publicacion").isNull()
                                ? null : src.path("anio_publicacion").asInt(),
                        textOrNull(src, "isbn13"),
                        textOrNull(src, "imagen_portada"),
                        textOrNull(src, "sinopsis"),
                        textOrNull(src, "categoria"),
                        textOrNull(src, "estado"),
                        textOrNull(src, "fecha_devolucion")
                );

                content.add(libro);
            }

            return new BusquedaResponseDTO(content, p, s, total);

        } catch (Exception e) {
            // Si algo falla, devolvemos vacío pero sin tumbar el micro
            return new BusquedaResponseDTO(List.of(), p, s, 0);
        }
    }

    // 2) Sugerencias (rápido): busca por prefijo de titulo (simple)
    public SugerenciasResponseDTO sugerencias(String q) {
        if (q == null || q.isBlank()) {
            return new SugerenciasResponseDTO(List.of());
        }

        String url = esUrl + "/" + index + "/_search";
        String body = """
        {
          "size": 5,
          "_source": ["titulo"],
          "query": {
            "match_phrase_prefix": {
              "titulo": "%s"
            }
          }
        }
        """.formatted(escape(q));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            JsonNode root = mapper.readTree(response.getBody());
            List<String> sug = new ArrayList<>();
            for (JsonNode h : root.path("hits").path("hits")) {
                String titulo = h.path("_source").path("titulo").asText(null);
                if (titulo != null && !sug.contains(titulo)) sug.add(titulo);
            }
            return new SugerenciasResponseDTO(sug);
        } catch (Exception e) {
            return new SugerenciasResponseDTO(List.of());
        }
    }

    // 3) Facets reales: agregación por categoria
    public FacetsResponseDTO facets() {
        String url = esUrl + "/" + index + "/_search";

        // Nota: si tu mapping tiene categoria como keyword o con subcampo .keyword, usa el que aplique.
        // Probamos primero "categoria.keyword" porque es lo típico cuando categoria es text.
        String body = """
        {
          "size": 0,
          "aggs": {
            "categorias": {
              "terms": { "field": "categoria.keyword", "size": 20 }
            }
          }
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JsonNode root = mapper.readTree(response.getBody());

            List<FacetDTO> cats = new ArrayList<>();
            for (JsonNode b : root.path("aggregations").path("categorias").path("buckets")) {
                String key = b.path("key").asText("");
                long count = b.path("doc_count").asLong(0);
                cats.add(new FacetDTO(key, count));
            }

            // Si tu FacetsResponseDTO tiene otra estructura y no coincide, dímelo y lo ajusto al DTO exacto que tengas.
            return new FacetsResponseDTO(cats);

        } catch (Exception e) {
            // Si falla por el .keyword, intenta con "categoria" directo cambiando el field.
            return new FacetsResponseDTO(List.of());
        }
    }

    private String textOrNull(JsonNode src, String field) {
        JsonNode n = src.path(field);
        if (n.isMissingNode() || n.isNull()) return null;
        return n.asText();
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
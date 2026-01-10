package com.unir.biblioteca.buscador.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/buscador")
public class LibrosSearchController {

    private final RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    private String elasticUrl;

    @Value("${elasticsearch.index}")
    private String elasticIndex;

    public LibrosSearchController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/libros")
    public Object buscar(@RequestParam String query) {
        String url = elasticUrl + "/" + elasticIndex + "/_search";

        Map<String, Object> body = Map.of(
                "size", 10,
                "query", Map.of(
                        "multi_match", Map.of(
                                "query", query,
                                "fields", new String[] { "titulo^3", "autor^2", "descripcion" }
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.postForObject(url, new HttpEntity<>(body, headers), Object.class);
    }
}

package com.unir.biblioteca.buscador.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

    @Value("${ELASTIC_ACCESS_KEY}")
    private String elasticKey;

    @Value("${ELASTIC_SECRET}")
    private String elasticSecret;


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
        System.out.println("URL usada para Elastic: " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setBasicAuth(elasticKey, elasticSecret);

        //String credentials = elasticKey + ":" + elasticSecret;
        //String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        //headers.set("Authorization", "Basic " + encoded);

        return restTemplate.postForObject(url, new HttpEntity<>(body, headers), Object.class);
    }
}

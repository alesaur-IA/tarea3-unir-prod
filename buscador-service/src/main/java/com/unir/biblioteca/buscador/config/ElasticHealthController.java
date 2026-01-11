package com.unir.biblioteca.buscador.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/buscador/elastic")
public class ElasticHealthController {

    private final RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    private String elasticUrl;

    @Value("${ELASTIC_ACCESS_KEY}")
    private String elasticKey;

    @Value("${ELASTIC_SECRET}")
    private String elasticSecret;

    public ElasticHealthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/health")
    public Object health() {
        // Llamada directa a Elasticsearch
        return restTemplate.getForObject(elasticUrl, Object.class);
    }
}

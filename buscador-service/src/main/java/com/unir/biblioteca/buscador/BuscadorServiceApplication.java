package com.unir.biblioteca.buscador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class BuscadorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuscadorServiceApplication.class, args);
    }
}

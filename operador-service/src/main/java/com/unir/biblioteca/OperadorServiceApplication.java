package com.unir.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OperadorServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(OperadorServiceApplication.class, args);
  }
}

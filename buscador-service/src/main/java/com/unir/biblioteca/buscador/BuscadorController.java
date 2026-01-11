package com.unir.biblioteca.buscador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.biblioteca.buscador.dto.BusquedaResponseDTO;
import com.unir.biblioteca.buscador.dto.FacetsResponseDTO;
import com.unir.biblioteca.buscador.dto.LibroDTO;
import com.unir.biblioteca.buscador.dto.SugerenciasResponseDTO;
import com.unir.biblioteca.buscador.service.BuscadorService;

import java.io.InputStream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/libros")
public class BuscadorController {

    private final BuscadorService buscadorService;

    public BuscadorController(BuscadorService buscadorService) {
        this.buscadorService = buscadorService;
    }
    @GetMapping("/ping")
    public String ping() {
        return "MS Buscador funcionando";
    }

    // 🔍 1) BUSCAR LIBROS (REAL CON ELASTICSEARCH)
    @GetMapping("/buscar")
    public BusquedaResponseDTO buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return buscadorService.buscar(q, page, size);
    }

    // 💡 2) SUGERENCIAS
    @GetMapping("/sugerencias")
    public SugerenciasResponseDTO sugerencias(
            @RequestParam(required = false) String q
    ) {
        return buscadorService.sugerencias(q);
    }

    // 🧩 3) FACETS
    @GetMapping("/facets")
    public FacetsResponseDTO facets() {
        return buscadorService.facets();
    }

    @PostMapping("/seed")
    public String seed() throws Exception {

        // Leer JSON desde resources
        InputStream is = getClass().getClassLoader().getResourceAsStream("books.json");
        ObjectMapper mapper = new ObjectMapper();
        LibroDTO[] libros = mapper.readValue(is, LibroDTO[].class);

        // Elastic muy simple (ajusta host)
        String elasticUrl = "http://localhost:9200/libros/_doc/";

        RestTemplate rest = new RestTemplate();

        // Subir uno por uno
        for (LibroDTO libro : libros) {
            try {
                rest.postForObject(elasticUrl + libro.id(), libro, String.class);
                System.out.println("✔ subido id=" + libro.id());
            } catch (Exception e) {
                System.out.println("✖ error id=" + libro.id() + " -> " + e.getMessage());
            }
        }

        return "seed listo";
    }
}

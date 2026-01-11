package com.unir.biblioteca.buscador;

import com.unir.biblioteca.buscador.dto.BusquedaResponseDTO;
import com.unir.biblioteca.buscador.dto.FacetsResponseDTO;
import com.unir.biblioteca.buscador.dto.SugerenciasResponseDTO;
import com.unir.biblioteca.buscador.service.BuscadorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    
}

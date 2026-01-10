package com.unir.biblioteca.buscador.dto;

import java.util.List;

public record BusquedaResponseDTO(
        List<LibroDTO> content,
        int page,
        int size,
        long total
) {}

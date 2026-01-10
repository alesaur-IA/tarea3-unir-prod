package com.unir.biblioteca.buscador.dto;

public record LibroDTO(
        String id,
        String titulo,
        String autor,
        Integer anio_publicacion,
        String isbn13,
        String imagen_portada,
        String sinopsis,
        String categoria,
        String estado,
        String fecha_devolucion
) {}

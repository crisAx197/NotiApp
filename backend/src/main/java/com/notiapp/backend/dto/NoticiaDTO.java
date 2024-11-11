package com.notiapp.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NoticiaDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private List<String> categorias;
    private LocalDateTime fechaPublicacion;

    public NoticiaDTO(Long id, String titulo, String descripcion, List<String> categorias, LocalDateTime fechaPublicacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categorias = categorias;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Getters y Setters
}

package com.notiapp.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class NoticiaDTO {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String titulo;

    @JsonProperty
    private String imagen;

    @JsonProperty
    private String descripcion;

    @JsonProperty
    private List<String> categoria; 
    
    @JsonProperty
    private LocalDateTime fechaPublicacion;

    public NoticiaDTO(Long id, String titulo, String descripcion, List<String> categoria, LocalDateTime fechaPublicacion, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaPublicacion = fechaPublicacion;
        this.imagen = imagen;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}

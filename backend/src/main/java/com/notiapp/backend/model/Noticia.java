package com.notiapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "noticia")
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNoticia;

    private String titulo;
    private String descripcion;
    private String imagen;
    private String cuerpo;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime ultFechaEdicion;

    @OneToMany(mappedBy = "noticia", cascade = CascadeType.ALL)
    private List<CategoriaNoticia> categorias;

    // Getters y Setters
    public Long getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(Long idNoticia) {
        this.idNoticia = idNoticia;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getUltFechaEdicion() {
        return ultFechaEdicion;
    }

    public void setUltFechaEdicion(LocalDateTime ultFechaEdicion) {
        this.ultFechaEdicion = ultFechaEdicion;
    }

    public List<CategoriaNoticia> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaNoticia> categorias) {
        this.categorias = categorias;
    }
}

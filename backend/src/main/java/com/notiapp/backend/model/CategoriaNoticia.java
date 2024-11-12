package com.notiapp.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "categoria_noticia")
public class CategoriaNoticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoriaNoticia;

    private String categoria;

    @ManyToOne
    @JoinColumn(name = "id_noticia")
    @JsonBackReference
    private Noticia noticia;

    // Getters y Setters
    public Long getIdCategoriaNoticia() {
        return idCategoriaNoticia;
    }

    public void setIdCategoriaNoticia(Long idCategoriaNoticia) {
        this.idCategoriaNoticia = idCategoriaNoticia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }
}

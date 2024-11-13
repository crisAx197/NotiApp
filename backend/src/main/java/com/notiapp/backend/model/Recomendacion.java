package com.notiapp.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recomendacion")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecomendacion;

    @ManyToOne
    @JoinColumn(name = "id_categoria_noticia", nullable = false)
    private CategoriaNoticia categoriaNoticia;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Getters y Setters
    public Long getIdRecomendacion() {
        return idRecomendacion;
    }

    public void setIdRecomendacion(Long idRecomendacion) {
        this.idRecomendacion = idRecomendacion;
    }

    public CategoriaNoticia getCategoriaNoticia() {
        return categoriaNoticia;
    }

    public void setCategoriaNoticia(CategoriaNoticia categoriaNoticia) {
        this.categoriaNoticia = categoriaNoticia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

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
}

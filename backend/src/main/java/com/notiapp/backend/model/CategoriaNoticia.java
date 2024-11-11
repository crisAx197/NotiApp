package com.notiapp.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_noticia")
public class CategoriaNoticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoriaNoticia;

    private String categoria;

    @ManyToOne
    @JoinColumn(name = "id_noticia", nullable = false)
    private Noticia noticia;

}

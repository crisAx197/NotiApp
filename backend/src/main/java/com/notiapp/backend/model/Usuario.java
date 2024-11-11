package com.notiapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String correo;
    private String password;
    private boolean cuentaActiva;
    private int edad;
    private LocalDateTime fechaRegistro;
    private String tipoUsuario;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Recomendacion> recomendaciones;

    // Getters y Setters
}

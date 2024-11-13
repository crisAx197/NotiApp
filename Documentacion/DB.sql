-- Script DB Notiapp
CREATE DATABASE Notiapp;
GO

Use Notiapp;
GO


-- Tabla noticia
CREATE TABLE noticia (
    id_noticia INT PRIMARY KEY IDENTITY,
    titulo NVARCHAR(255) NOT NULL,
    descripcion NVARCHAR(500),
    imagen NVARCHAR(255),
    cuerpo TEXT,
    fecha_publicacion DATETIME NOT NULL,
    ult_fecha_edicion DATETIME
);

-- Tabla categoria_noticia
CREATE TABLE categoria_noticia (
    id_categoria_noticia INT PRIMARY KEY IDENTITY,
    id_noticia INT FOREIGN KEY REFERENCES noticia(id_noticia) ON DELETE CASCADE,
    categoria NVARCHAR(100) NOT NULL
);

-- Tabla usuario
CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY IDENTITY,
    nombre NVARCHAR(100) NOT NULL,
    correo NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    cuenta_activa BIT DEFAULT 1,
    edad INT,
    fecha_registro DATETIME DEFAULT GETDATE(),
    tipo_usuario NVARCHAR(50)
);

-- Tabla recomendacion
CREATE TABLE recomendacion (
    id_recomendacion INT PRIMARY KEY IDENTITY,
    id_categoria_noticia INT FOREIGN KEY REFERENCES categoria_noticia(id_categoria_noticia) ON DELETE CASCADE,
    id_usuario INT FOREIGN KEY REFERENCES usuario(id_usuario) ON DELETE CASCADE
);



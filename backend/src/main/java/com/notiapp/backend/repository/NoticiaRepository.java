package com.notiapp.backend.repository;

import com.notiapp.backend.dto.NoticiaDTO;
import com.notiapp.backend.model.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    @Query("SELECT new com.notiapp.backend.dto.NoticiaDTO(n.idNoticia, n.titulo, n.descripcion, c.categoria, n.fechaPublicacion) " +
           "FROM Noticia n LEFT JOIN n.categorias c " +
           "ORDER BY n.fechaPublicacion DESC")
    List<NoticiaDTO> findTop10ByOrderByFechaPublicacionDesc();
}

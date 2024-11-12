package com.notiapp.backend.repository;

import com.notiapp.backend.model.Noticia;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    @Query("SELECT n FROM Noticia n JOIN n.categorias c WHERE c.categoria IN " +
           "(SELECT r.categoriaNoticia.categoria FROM Recomendacion r WHERE r.usuario.idUsuario = :userId) " +
           "ORDER BY n.fechaPublicacion DESC")
    List<Noticia> findRecommendedNewsByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Noticia> findTop10ByOrderByFechaPublicacionDesc();
    
    @Query("SELECT n FROM Noticia n JOIN n.categorias c " +
       "WHERE (:categoria IS NULL OR c.categoria = :categoria) " +
       "AND (:startDate IS NULL OR n.fechaPublicacion >= :startDate) " +
       "AND (:endDate IS NULL OR n.fechaPublicacion <= :endDate) " +
       "ORDER BY n.fechaPublicacion DESC")
    List<Noticia> findByCategoryAndDateRange(
            @Param("categoria") String categoria,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

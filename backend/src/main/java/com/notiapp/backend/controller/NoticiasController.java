package com.notiapp.backend.controller;

import com.notiapp.backend.dto.NoticiaDTO;
import com.notiapp.backend.model.Noticia;
import com.notiapp.backend.repository.NoticiaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/news")
public class NoticiasController {

    private final NoticiaRepository noticiaRepository;

    public NoticiasController(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    @GetMapping("/lastNews")
    public List<NoticiaDTO> getNoticias() {
        List<Noticia> noticias = noticiaRepository.findTop10ByOrderByFechaPublicacionDesc();
        
        return noticias.stream().map(noticia -> 
            new NoticiaDTO(
                noticia.getIdNoticia(),
                noticia.getTitulo(),
                noticia.getDescripcion(),
                noticia.getCategorias().stream().map(c -> c.getCategoria()).collect(Collectors.toList()),
                noticia.getFechaPublicacion(),
                noticia.getImagen()
            )
        ).collect(Collectors.toList());
    }

    @GetMapping("/recommendations/{userId}")
    public List<NoticiaDTO> getRecommendedNews(@PathVariable Long userId) {
        List<Noticia> recommendedNoticias = noticiaRepository.findRecommendedNewsByUserId(userId, PageRequest.of(0, 3));

        return recommendedNoticias.stream().map(noticia ->
            new NoticiaDTO(
                noticia.getIdNoticia(),
                noticia.getTitulo(),
                noticia.getDescripcion(),
                noticia.getCategorias().stream().map(c -> c.getCategoria()).collect(Collectors.toList()),
                noticia.getFechaPublicacion(),
                noticia.getImagen()
            )
        ).collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public Noticia getNoticiaById(@PathVariable Long id) {
        return noticiaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Noticia not found with id " + id));
    }
}

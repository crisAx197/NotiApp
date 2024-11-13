package com.notiapp.backend.controller;

import com.notiapp.backend.dto.NoticiaDTO;
import com.notiapp.backend.model.Noticia;
import com.notiapp.backend.repository.NoticiaRepository;
import com.notiapp.backend.util.JwtUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/news")
public class NoticiasController {

    private final NoticiaRepository noticiaRepository;
    private final JwtUtil jwtUtil;

    public NoticiasController(NoticiaRepository noticiaRepository, JwtUtil jwtUtil) {
        this.noticiaRepository = noticiaRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/lastNews")
    public List<NoticiaDTO> getNoticias(@RequestHeader("Authorization") String token) {
        validateToken(token);
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
    public List<NoticiaDTO> getRecommendedNews(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        validateToken(token);
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
    public Noticia getNoticiaById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        validateToken(token);
        return noticiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Noticia not found with id " + id));
    }

    @GetMapping("/filter")
    public List<NoticiaDTO> getFilteredNews(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestHeader("Authorization") String token) {
        
        validateToken(token);
        List<Noticia> filteredNoticias = noticiaRepository.findByCategoryAndDateRange(categoria, startDate, endDate);

        return filteredNoticias.stream().map(noticia ->
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

    private void validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is missing or invalid");
        }

        String jwtToken = token.substring(7); 
        String email = jwtUtil.extractEmail(jwtToken);

        if (!jwtUtil.isTokenValid(jwtToken, email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }
}

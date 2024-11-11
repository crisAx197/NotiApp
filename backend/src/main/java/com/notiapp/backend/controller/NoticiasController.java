package com.notiapp.backend.controller;

import com.notiapp.backend.dto.NoticiaDTO;
import com.notiapp.backend.repository.NoticiaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/noticias")
public class NoticiasController {

    private final NoticiaRepository noticiaRepository;

    public NoticiasController(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    @GetMapping("/lastNews")
    public List<NoticiaDTO> getNoticias() {
        return noticiaRepository.findTop10ByOrderByFechaPublicacionDesc();
    }
}

package com.notiapp.backend.controller;

import com.notiapp.backend.dto.UsuarioDTO;
import com.notiapp.backend.model.Usuario;
import com.notiapp.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuariosController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setCorreo(usuarioDTO.getCorreo());
            usuario.setEdad(usuarioDTO.getEdad());
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

            Usuario savedUsuario = usuarioRepository.save(usuario);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", savedUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException e) {
            response.put("status", HttpStatus.CONFLICT.value());
            response.put("message", "El correo '" + usuarioDTO.getCorreo() + "' ya está en uso.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error al registrar el usuario. Intente nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

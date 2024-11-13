package com.notiapp.backend.controller;

import com.notiapp.backend.dto.UsuarioDTO;
import com.notiapp.backend.model.Usuario;
import com.notiapp.backend.repository.UsuarioRepository;
import com.notiapp.backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil; 

    @Autowired
    public UsuariosController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
    }

    // Registro de Usuario
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setCorreo(usuarioDTO.getCorreo());
            usuario.setEdad(usuarioDTO.getEdad());
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            usuario.setCuentaActiva(false); 

            Usuario savedUsuario = usuarioRepository.save(usuario);
            String activationToken = savedUsuario.getIdUsuario().toString();
            enviarCorreoActivacion(savedUsuario.getCorreo(), activationToken);

            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Usuario registrado exitosamente. Por favor, revisa tu correo para activar tu cuenta.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException e) {
            response.put("status", HttpStatus.CONFLICT.value());
            response.put("message", "El correo '" + usuarioDTO.getCorreo() + "' ya está en uso.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Ocurrió un error inesperado durante el registro. Por favor, intenta nuevamente en unos minutos.");
            response.put("error", e.getClass().getName() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enviar correo de activación
    private void enviarCorreoActivacion(String correo, String token) throws MessagingException {
        String linkActivacion = "http://localhost:8080/usuarios/activate?token=" + token;
        String subject = "Activa tu cuenta";
        String content = "<p>Hola,</p>" +
                         "<p>Gracias por registrarte. Por favor, haz clic en el siguiente enlace para activar tu cuenta:</p>" +
                         "<a href=\"" + linkActivacion + "\">Activar cuenta</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        String fromAddress = "noreply@dominio.com";
        helper.setFrom(fromAddress); 
        helper.setTo(correo);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    // Activar cuenta
    @GetMapping("/activate")
    public ResponseEntity<Map<String, Object>> activarCuenta(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long usuarioId = Long.parseLong(token);
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token inválido o usuario no encontrado"));

            if (usuario.getCuentaActiva()) {
                response.put("status", HttpStatus.OK.value());
                response.put("message", "La cuenta ya está activada.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            usuario.setCuentaActiva(true);
            usuarioRepository.save(usuario);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Cuenta activada exitosamente.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (NumberFormatException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Token inválido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            Usuario usuario = usuarioRepository.findByCorreo(usuarioDTO.getCorreo());

            if (usuario != null && passwordEncoder.matches(usuarioDTO.getPassword(), usuario.getPassword())) {
                
                String token = jwtUtil.generateToken(usuario.getCorreo());
                response.put("status", HttpStatus.OK.value());
                response.put("message", "Login exitoso");
                response.put("token", token);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "Correo o contraseña incorrectos");

                // Añadiendo mensajes de depuración
                if (usuario == null) {
                    response.put("debug", "No se encontró un usuario con el correo proporcionado.");
                } else if (!passwordEncoder.matches(usuarioDTO.getPassword(), usuario.getPassword())) {
                    response.put("debug", "La contraseña proporcionada no coincide.");
                }

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Ocurrió un error durante el proceso de login. Intente nuevamente.");
            response.put("error", e.getClass().getName());  // Clase del error
            response.put("errorMessage", e.getMessage());   // Mensaje detallado del error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Recuperación de Contraseña
    @PostMapping("/recover-password")
    public ResponseEntity<Map<String, Object>> recuperarContrasenia(@RequestParam("correo") String correo) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null) {
            String resetToken = usuario.getIdUsuario().toString();
            try {
                enviarCorreoRecuperacion(correo, resetToken);
                response.put("status", HttpStatus.OK.value());
                response.put("message", "Correo de recuperación enviado. Revisa tu bandeja de entrada.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (MessagingException e) {
                response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.put("message", "Error al enviar el correo de recuperación. Intenta nuevamente.");
                response.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "El correo no está registrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Restablecimiento de Contraseña
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> restablecerContrasenia(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long usuarioId = Long.parseLong(token);
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token inválido o usuario no encontrado"));

            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Contraseña restablecida exitosamente.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (NumberFormatException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Token inválido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Enviar correo de recuperación
    private void enviarCorreoRecuperacion(String correo, String token) throws MessagingException {
        String linkRecuperacion = "http://localhost:8080/usuarios/reset-password?token=" + token;
        String subject = "Recupera tu contraseña";
        String content = "<p>Hola,</p>" +
                         "<p>Parece que solicitaste un restablecimiento de contraseña. Haz clic en el siguiente enlace para continuar:</p>" +
                         "<a href=\"" + linkRecuperacion + "\">Restablecer contraseña</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom("noreply@dominio.com");
        helper.setTo(correo);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}

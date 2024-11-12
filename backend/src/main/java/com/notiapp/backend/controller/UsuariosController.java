package com.notiapp.backend.controller;

import com.notiapp.backend.dto.UsuarioDTO;
import com.notiapp.backend.model.Usuario;
import com.notiapp.backend.repository.UsuarioRepository;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Autowired
    public UsuariosController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
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
            response.put("error", e.getClass().getName() + ": " + e.getMessage()); // Clase y mensaje de la excepción
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    }

    private void enviarCorreoActivacion(String correo, String token) throws MessagingException {
        String linkActivacion = "http://localhost:8080/usuarios/activate?token=" + token;
        String subject = "Activa tu cuenta";
        String content = "<p>Hola,</p>" +
                         "<p>Gracias por registrarte. Por favor, haz clic en el siguiente enlace para activar tu cuenta:</p>" +
                         "<a href=\"" + linkActivacion + "\">Activar cuenta</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        // Configurar el remitente
        String fromAddress = "noreply@tu-dominio.com"; // Reemplaza con tu dirección de remitente
        helper.setFrom(fromAddress); 

        // Configurar destinatario, asunto y contenido
        helper.setTo(correo);
        helper.setSubject(subject);
        helper.setText(content, true); // `true` indica que el contenido es HTML

        // Enviar correo y manejar excepciones
        try {
            mailSender.send(message);
            System.out.println("Correo de activación enviado a: " + correo);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo de activación: " + e.getMessage());
            e.printStackTrace(); // Muestra el stack trace en la consola para mayor detalle
            throw e; // Re-lanza la excepción para que sea manejada por el llamador
        }
    }



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
        Usuario usuario = usuarioRepository.findByCorreo(usuarioDTO.getCorreo());

        if (usuario != null && passwordEncoder.matches(usuarioDTO.getPassword(), usuario.getPassword())) {
            if (!usuario.getCuentaActiva()) {
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "La cuenta aún no está activada. Revisa tu correo.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login exitoso");
            response.put("usuario", usuario);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Correo o contraseña incorrectos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

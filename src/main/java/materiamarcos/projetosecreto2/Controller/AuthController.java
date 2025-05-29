package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.LoginRequestDTO;
import materiamarcos.projetosecreto2.DTOs.RegistroRequestDTO;
import materiamarcos.projetosecreto2.DTOs.UsuarioResponseDTO;
import materiamarcos.projetosecreto2.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Todos os endpoints começarão com /api/auth
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequestDTO registroRequestDTO) {
        try {
            UsuarioResponseDTO usuarioRegistrado = authService.registrarUsuario(registroRequestDTO);
            return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); // HTTP 201 Created
        } catch (RuntimeException ex) {
            // Tratamento de erro básico. @ControllerAdvice para exceções globais.
            return ResponseEntity
                    .badRequest() // HTTP 400 Bad Request
                    .body(ex.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            UsuarioResponseDTO usuarioLogado = authService.loginUsuario(loginRequestDTO);
            // Por enquanto, retornamos apenas os dados do usuário.
            // Depois, vamos retornar um TokenDTO aqui.
            return ResponseEntity.ok(usuarioLogado);
        } catch (BadCredentialsException ex) {
            // Retorna 401 para credenciais inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro: Credenciais inválidas.");
        } catch (Exception ex) {
            // Outros erros
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar login: " + ex.getMessage());
        }
    }

}
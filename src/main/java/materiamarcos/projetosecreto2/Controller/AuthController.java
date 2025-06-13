package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.JwtResponseDTO;
import materiamarcos.projetosecreto2.DTOs.LoginRequestDTO;
import materiamarcos.projetosecreto2.DTOs.RegistroRequestDTO;
import materiamarcos.projetosecreto2.DTOs.UsuarioResponseDTO;
import materiamarcos.projetosecreto2.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Tag(name = " Autenticação", description = "Endpoints para registro e login de usuários")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Método helper para criar respostas de erro padronizadas
    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Registrar um novo usuário",
            description = "Cria um novo usuário no sistema com um papel padrão de 'CLIENTE'. O nome de usuário e o email devem ser únicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou nome de usuário/email já em uso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequestDTO registroRequestDTO, HttpServletRequest request) {
        try {
            UsuarioResponseDTO usuarioRegistrado = authService.registrarUsuario(registroRequestDTO);
            return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), request);
        }
    }

    @Operation(summary = "Autenticar um usuário e obter um token JWT",
            description = "Realiza o login do usuário com nome de usuário e senha, retornando um token JWT para ser usado em endpoints protegidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)) })
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        try {
            String jwtToken = authService.loginUsuarioERetornarToken(loginRequestDTO);
            return ResponseEntity.ok(new JwtResponseDTO(jwtToken));
        } catch (BadCredentialsException ex) {
            return criarErrorResponse(HttpStatus.UNAUTHORIZED, "Não Autorizado", "Credenciais inválidas.", request);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro ao processar o login.", request);
        }
    }
}

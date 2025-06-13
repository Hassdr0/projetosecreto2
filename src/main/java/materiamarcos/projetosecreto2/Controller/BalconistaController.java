package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.BalconistaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.BalconistaResponseDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.Service.BalconistaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Balconistas", description = "Endpoints para gerenciamento de balconistas/funcionários")
@RestController
@RequestMapping("/api/balconistas")
@SecurityRequirement(name = "bearerAuth") // Aplica segurança JWT a todos os endpoints desta classe
public class BalconistaController {

    @Autowired
    private BalconistaService balconistaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Criar um novo balconista",
            description = "Registra um novo balconista no sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Balconista criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalconistaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já existente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarBalconista(@Valid @RequestBody BalconistaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO salvo = balconistaService.criarBalconista(requestDTO);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar balconista.", httpRequest);
        }
    }

    @Operation(summary = "Listar todos os balconistas",
            description = "Retorna uma lista de todos os balconistas cadastrados. Requer permissão de ADMIN ou GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de balconistas retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<BalconistaResponseDTO>> listarTodosBalconistas() {
        List<BalconistaResponseDTO> lista = balconistaService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar um balconista por ID",
            description = "Retorna os detalhes de um balconista específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balconista encontrado"),
            @ApiResponse(responseCode = "404", description = "Balconista não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarBalconistaPorId(
            @Parameter(description = "ID do balconista a ser buscado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO dto = balconistaService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar balconista.", httpRequest);
        }
    }

    @Operation(summary = "Atualizar um balconista existente",
            description = "Atualiza os dados de um balconista. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balconista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Balconista não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já existe em outro registro")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarBalconista(
            @Parameter(description = "ID do balconista a ser atualizado", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BalconistaRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO atualizado = balconistaService.atualizarBalconista(id, requestDTO);
            return ResponseEntity.ok(atualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar balconista.", httpRequest);
        }
    }

    @Operation(summary = "Deletar um balconista",
            description = "Remove um balconista do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Balconista deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Balconista não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito - O balconista não pode ser deletado pois possui vendas associadas")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarBalconista(
            @Parameter(description = "ID do balconista a ser deletado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            balconistaService.deletarBalconista(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar balconista.", httpRequest);
        }
    }
}

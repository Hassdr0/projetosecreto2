package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
import materiamarcos.projetosecreto2.Service.PrincipioAtivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Princípios Ativos", description = "Endpoints para gerenciamento de princípios ativos dos medicamentos")
@RestController
@RequestMapping("/api/principios-ativos")
@SecurityRequirement(name = "bearerAuth")
public class PrincipioAtivoController {

    @Autowired
    private PrincipioAtivoService principioAtivoService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Criar um novo princípio ativo",
            description = "Registra um novo princípio ativo no sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Princípio ativo criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrincipioAtivoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já existente"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarPrincipioAtivo(@Valid @RequestBody PrincipioAtivoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO salvo = principioAtivoService.criarPrincipioAtivo(requestDTO);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar princípio ativo.", httpRequest);
        }
    }

    @Operation(summary = "Listar todos os princípios ativos",
            description = "Retorna uma lista de todos os princípios ativos cadastrados. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de princípios ativos retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrincipioAtivoResponseDTO>> listarTodos() {
        List<PrincipioAtivoResponseDTO> lista = principioAtivoService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar um princípio ativo por ID",
            description = "Retorna os detalhes de um princípio ativo específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Princípio ativo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrincipioAtivoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Princípio ativo não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID do princípio ativo a ser buscado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO dto = principioAtivoService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar princípio ativo.", httpRequest);
        }
    }

    @Operation(summary = "Atualizar um princípio ativo existente",
            description = "Atualiza o nome de um princípio ativo. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Princípio ativo atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrincipioAtivoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Princípio ativo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já existe em outro registro")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarPrincipioAtivo(
            @Parameter(description = "ID do princípio ativo a ser atualizado", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PrincipioAtivoRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO atualizado = principioAtivoService.atualizarPrincipioAtivo(id, requestDTO);
            return ResponseEntity.ok(atualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar princípio ativo.", httpRequest);
        }
    }

    @Operation(summary = "Deletar um princípio ativo",
            description = "Remove um princípio ativo do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Princípio ativo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Princípio ativo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito - O princípio ativo não pode ser deletado pois está em uso por medicamentos")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarPrincipioAtivo(
            @Parameter(description = "ID do princípio ativo a ser deletado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            principioAtivoService.deletarPrincipioAtivo(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Não foi possível deletar: Princípio Ativo está em uso.", httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar princípio ativo.", httpRequest);
        }
    }
}

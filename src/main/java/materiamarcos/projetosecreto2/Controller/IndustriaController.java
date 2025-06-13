package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaDTO;
import materiamarcos.projetosecreto2.Service.IndustriaService;
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

@Tag(name = " Indústrias", description = "Endpoints para gerenciamento de indústrias/fornecedores farmacêuticos")
@RestController
@RequestMapping("/api/industrias")
@SecurityRequirement(name = "bearerAuth")
public class IndustriaController {

    @Autowired
    private IndustriaService industriaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Criar uma nova indústria",
            description = "Registra uma nova indústria/fornecedor no sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Indústria criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome/CNPJ já existente"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarIndustria(@Valid @RequestBody IndustriaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            IndustriaDTO salva = industriaService.criarIndustria(requestDTO);
            return new ResponseEntity<>(salva, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar indústria.", httpRequest);
        }
    }

    @Operation(summary = "Listar todas as indústrias",
            description = "Retorna uma lista de todas as indústrias cadastradas. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de indústrias retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IndustriaDTO>> listarTodas() {
        List<IndustriaDTO> lista = industriaService.listarTodas();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar uma indústria por ID",
            description = "Retorna os detalhes de uma indústria específica. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indústria encontrada"),
            @ApiResponse(responseCode = "404", description = "Indústria não encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID da indústria a ser buscada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            IndustriaDTO dto = industriaService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar indústria.", httpRequest);
        }
    }

    @Operation(summary = "Atualizar uma indústria existente",
            description = "Atualiza os dados de uma indústria. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indústria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Indústria não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome/CNPJ já existe em outro registro")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarIndustria(
            @Parameter(description = "ID da indústria a ser atualizada", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody IndustriaRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            IndustriaDTO atualizada = industriaService.atualizarIndustria(id, requestDTO);
            return ResponseEntity.ok(atualizada);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar indústria.", httpRequest);
        }
    }

    @Operation(summary = "Deletar uma indústria",
            description = "Remove uma indústria do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Indústria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Indústria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito - A indústria não pode ser deletada pois está em uso")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarIndustria(
            @Parameter(description = "ID da indústria a ser deletada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            industriaService.deletarIndustria(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar indústria.", httpRequest);
        }
    }
}

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
import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.Service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Medicamentos", description = "Endpoints para gerenciamento de medicamentos")
@RestController
@RequestMapping("/api/medicamentos")
@SecurityRequirement(name = "bearerAuth") // Aplica segurança JWT a todos os endpoints desta classe
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Criar um novo medicamento",
            description = "Registra um novo medicamento no sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicamento criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicamentoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou código de barras já existente"),
            @ApiResponse(responseCode = "404", description = "Princípio Ativo ou Indústria não encontrados")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarNovoMedicamento(@Valid @RequestBody MedicamentoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamentoSalvo = medicamentoService.criarMedicamento(requestDTO);
            return new ResponseEntity<>(medicamentoSalvo, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao criar o medicamento.", httpRequest);
        }
    }

    @Operation(summary = "Listar todos os medicamentos",
            description = "Retorna uma lista de todos os medicamentos cadastrados. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de medicamentos retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MedicamentoResponseDTO>> listarTodosMedicamentos() {
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarTodos();
        return ResponseEntity.ok(medicamentos);
    }

    @Operation(summary = "Buscar um medicamento por ID",
            description = "Retorna os detalhes de um medicamento específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarMedicamentoPorId(
            @Parameter(description = "ID do medicamento a ser buscado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamento = medicamentoService.buscarPorId(id);
            return ResponseEntity.ok(medicamento);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao buscar o medicamento.", httpRequest);
        }
    }

    @Operation(summary = "Atualizar um medicamento existente",
            description = "Atualiza os dados de um medicamento, incluindo suas regras de promoção. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Medicamento, Princípio Ativo ou Indústria não encontrados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, violação de regra de negócio (ex: promoção) ou código de barras já em uso")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarMedicamento(
            @Parameter(description = "ID do medicamento a ser atualizado", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamentoAtualizado = medicamentoService.atualizarMedicamento(id, requestDTO);
            return ResponseEntity.ok(medicamentoAtualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao atualizar o medicamento.", httpRequest);
        }
    }

    @Operation(summary = "Deletar um medicamento",
            description = "Remove um medicamento do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medicamento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito - O medicamento não pode ser deletado pois está em uso (ex: em vendas ou estoque)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarMedicamento(
            @Parameter(description = "ID do medicamento a ser deletado", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            medicamentoService.deletarMedicamento(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch(DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Não foi possível deletar o medicamento, pois ele está associado a outros registros.", httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Não foi possível deletar o medicamento.", httpRequest);
        }
    }
}

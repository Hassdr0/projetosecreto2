package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.AjusteEstoqueRequestDTO;
import materiamarcos.projetosecreto2.DTOs.AlertaEstoqueDTO;
import materiamarcos.projetosecreto2.DTOs.EntradaEstoqueRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.EstoqueResponseDTO;
import materiamarcos.projetosecreto2.Service.EstoqueService;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Tag(name = " Estoque", description = "Endpoints para gerenciamento de estoque de medicamentos")
@RestController
@RequestMapping("/api/estoque")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Registrar entrada de estoque",
            description = "Registra a chegada de novos itens no estoque de uma filial. Se o item/lote já existir na filial, a quantidade é somada. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada de estoque registrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstoqueResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento ou Farmácia não encontrados"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    })
    @PostMapping("/entrada")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarEntradaNoEstoque(
            @Valid @RequestBody EntradaEstoqueRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            EstoqueResponseDTO estoqueAtualizado = estoqueService.registrarEntradaEstoque(requestDTO);
            return new ResponseEntity<>(estoqueAtualizado, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Erro de integridade de dados ao registrar entrada no estoque.", httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao registrar entrada no estoque.", httpRequest);
        }
    }

    @Operation(summary = "Ajustar estoque manualmente",
            description = "Realiza um ajuste manual no estoque (entrada ou saída/baixa). Para baixa, a quantidade deve ser negativa. Requer permissão de ADMIN ou GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajuste realizado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito - Estoque insuficiente para a baixa solicitada")
    })
    @PostMapping("/ajuste")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<?> ajustarEstoque(
            @Valid @RequestBody AjusteEstoqueRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            EstoqueResponseDTO estoqueAjustado = estoqueService.ajustarEstoque(requestDTO);
            return ResponseEntity.ok(estoqueAjustado);
        } catch (EstoqueInsuficienteException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Estoque insuficiente", ex.getMessage(), httpRequest);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao ajustar o estoque.", httpRequest);
        }
    }

    @Operation(summary = "Listar todo o estoque de uma filial",
            description = "Retorna uma lista de todos os registros de estoque para uma filial específica. Requer autenticação.")
    @GetMapping("/filial/{farmaciaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarEstoquePorFarmacia(
            @Parameter(description = "ID da farmácia/filial a ser consultada", required = true, example = "1")
            @PathVariable Long farmaciaId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorFarmacia(farmaciaId);
            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao listar o estoque da filial.", httpRequest);
        }
    }

    @Operation(summary = "Listar estoque de um medicamento em todas as filiais",
            description = "Retorna uma lista de todos os registros de estoque (lotes/filiais) para um medicamento específico. Requer autenticação.")
    @GetMapping("/medicamento/{medicamentoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarEstoquePorMedicamento(
            @Parameter(description = "ID do medicamento a ser consultado", required = true, example = "1")
            @PathVariable Long medicamentoId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorMedicamento(medicamentoId);
            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao listar o estoque do medicamento.", httpRequest);
        }
    }

    @Operation(summary = "Buscar estoque de um medicamento em uma filial específica",
            description = "Retorna os registros de estoque (lotes) de um medicamento específico em uma filial. Requer autenticação.")
    @GetMapping("/medicamento/{medicamentoId}/filial/{farmaciaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarEstoquePorMedicamentoEFarmacia(
            @Parameter(description = "ID do medicamento", required = true, example = "1") @PathVariable Long medicamentoId,
            @Parameter(description = "ID da farmácia/filial", required = true, example = "1") @PathVariable Long farmaciaId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorMedicamentoEFarmacia(medicamentoId, farmaciaId);
            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao buscar o estoque.", httpRequest);
        }
    }

    @Operation(summary = "Verificar alertas de estoque mínimo",
            description = "Retorna uma lista de princípios ativos cujo estoque total está abaixo do nível mínimo definido. Requer permissão de ADMIN ou GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum alerta de estoque baixo encontrado")
    })
    @GetMapping("/alertas-minimo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<AlertaEstoqueDTO>> getAlertasEstoqueMinimo() {
        List<AlertaEstoqueDTO> alertas = estoqueService.verificarAlertasEstoqueMinimo();
        if (alertas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alertas);
    }
}

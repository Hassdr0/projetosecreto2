package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.CotacaoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.CotacaoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.Service.CotacaoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Compras e Cotações", description = "Endpoints para o processo de aquisição de medicamentos")
@RestController
@RequestMapping("/api/cotacoes")
@SecurityRequirement(name = "bearerAuth")
public class CotacaoController {

    @Autowired
    private CotacaoService cotacaoService;

    // Método helper para criar respostas de erro padronizadas
    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Registrar uma nova cotação",
            description = "Registra o preço cotado de um medicamento com uma indústria específica. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cotação registrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CotacaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento ou Indústria não encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarCotacao(@Valid @RequestBody CotacaoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            CotacaoResponseDTO cotacaoSalva = cotacaoService.registrarCotacao(requestDTO);
            return new ResponseEntity<>(cotacaoSalva, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro ao registrar a cotação.", httpRequest);
        }
    }

    @Operation(summary = "Buscar cotações ativas por medicamento",
            description = "Retorna uma lista de todas as cotações ativas para um medicamento específico, ordenadas pelo menor preço. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cotações retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/medicamento/{medicamentoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CotacaoResponseDTO>> buscarCotacoesPorMedicamento(
            @Parameter(description = "ID do medicamento para o qual buscar cotações", required = true, example = "1")
            @PathVariable Long medicamentoId) {
        List<CotacaoResponseDTO> cotacoes = cotacaoService.buscarCotacoesAtivasPorMedicamento(medicamentoId);
        return ResponseEntity.ok(cotacoes);
    }
}

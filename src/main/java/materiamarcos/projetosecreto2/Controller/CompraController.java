package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import materiamarcos.projetosecreto2.DTOs.CompraRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ConfirmarRecebimentoCompraDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.Model.Compra;
import materiamarcos.projetosecreto2.Service.CompraService;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Tag(name = " Compras e Cotações", description = "Endpoints para o processo de aquisição de medicamentos")
@RestController
@RequestMapping("/api/compras")
@SecurityRequirement(name = "bearerAuth")
public class CompraController {

    @Autowired
    private CompraService compraService;

    // Método helper para criar respostas de erro padronizadas
    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Criar um novo pedido de compra",
            description = "Inicia um pedido de compra para um medicamento. O sistema buscará a cotação mais barata e criará o pedido com o status 'PENDENTE_RECEBIMENTO'. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido de compra criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Compra.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhuma cotação encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarCompra(@Valid @RequestBody CompraRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            Compra novaCompra = compraService.criarCompraDeMelhorCotacao(requestDTO);
            return new ResponseEntity<>(novaCompra, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (RuntimeException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        }
    }

    @Operation(summary = "Confirmar recebimento de uma compra",
            description = "Confirma o recebimento de uma compra (ex: chegada da Nota Fiscal) e dá entrada dos itens no estoque. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra recebida e estoque atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Compra.class))),
            @ApiResponse(responseCode = "404", description = "Compra não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou a compra não pode ser recebida (status incorreto)")
    })
    @PostMapping("/{id}/receber")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> receberCompra(
            @Parameter(description = "ID da compra a ser confirmada", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarRecebimentoCompraDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            Compra compraRecebida = compraService.confirmarRecebimentoCompra(id, requestDTO);
            return ResponseEntity.ok(compraRecebida);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalStateException | EstoqueInsuficienteException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao confirmar recebimento da compra.", httpRequest);
        }
    }

    // TODO: Adicionar endpoints GET para listar e buscar compras
}

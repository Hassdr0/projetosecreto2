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
import materiamarcos.projetosecreto2.DTOs.VendaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.VendaResponseDTO;
import materiamarcos.projetosecreto2.Service.VendaService;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Vendas", description = "Endpoints para registro, consulta e cancelamento de vendas")
@RestController
@RequestMapping("/api/vendas")
@SecurityRequirement(name = "bearerAuth")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @Operation(summary = "Registrar uma nova venda",
            description = "Cria uma nova venda, valida o estoque e realiza a baixa dos itens vendidos. Requer permissão de ADMIN ou BALCONISTA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venda registrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VendaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da venda inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (ex: Cliente, Balconista, Medicamento ou Farmácia)"),
            @ApiResponse(responseCode = "409", description = "Conflito de dados (ex: Estoque insuficiente)")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BALCONISTA')")
    public ResponseEntity<?> registrarNovaVenda(@Valid @RequestBody VendaRequestDTO vendaRequestDTO, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usernameBalconistaLogado = authentication.getName();

            if (usernameBalconistaLogado == null || usernameBalconistaLogado.equals("anonymousUser")) {
                return criarErrorResponse(HttpStatus.UNAUTHORIZED, "Não Autorizado", "Nenhum usuário autenticado para registrar a venda.", httpRequest);
            }
            // A lógica de serviço usa o balconistaId do DTO, mas o username logado pode ser usado para validação adicional
            VendaResponseDTO vendaSalva = vendaService.registrarVenda(vendaRequestDTO, usernameBalconistaLogado);
            return new ResponseEntity<>(vendaSalva, HttpStatus.CREATED);

        } catch (EstoqueInsuficienteException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Estoque Insuficiente", ex.getMessage(), httpRequest);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao registrar a venda.", httpRequest);
        }
    }

    @Operation(summary = "Cancelar uma venda existente",
            description = "Muda o status de uma venda para 'CANCELADA' e reverte (estorna) a quantidade dos itens para o estoque. Requer permissão de ADMIN ou GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito - A venda não pode ser cancelada (ex: já está cancelada)")
    })
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<?> cancelarVenda(
            @Parameter(description = "ID da venda a ser cancelada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            VendaResponseDTO vendaCancelada = vendaService.cancelarVenda(id);
            return ResponseEntity.ok(vendaCancelada);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalStateException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Estado inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao cancelar a venda.", httpRequest);
        }
    }

    @Operation(summary = "Listar todas as vendas",
            description = "Retorna uma lista de todas as vendas registradas. Requer permissão de ADMIN ou GERENTE.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<VendaResponseDTO>> listarTodasVendas() {
        List<VendaResponseDTO> vendas = vendaService.listarTodasVendas();
        return ResponseEntity.ok(vendas);
    }

    @Operation(summary = "Buscar uma venda por ID",
            description = "Retorna os detalhes de uma venda específica, incluindo seus itens. Requer autenticação.")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarVendaPorId(
            @Parameter(description = "ID da venda a ser buscada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            VendaResponseDTO venda = vendaService.buscarVendaPorId(id);
            return ResponseEntity.ok(venda);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        }
    }

    @Operation(summary = "Listar vendas por balconista",
            description = "Retorna o histórico de vendas de um balconista específico. Requer permissão de ADMIN ou GERENTE.")
    @GetMapping("/balconista/{balconistaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<?> listarVendasPorBalconista(
            @Parameter(description = "ID do balconista para consultar o histórico", required = true, example = "1")
            @PathVariable Long balconistaId,
            HttpServletRequest httpRequest) {
        try {
            List<VendaResponseDTO> vendas = vendaService.listarVendasPorBalconista(balconistaId);
            return ResponseEntity.ok(vendas);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        }
    }

    @Operation(summary = "Listar vendas por cliente",
            description = "Retorna o histórico de compras de um cliente específico. Requer permissão de ADMIN ou BALCONISTA.")
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BALCONISTA')")
    public ResponseEntity<?> listarVendasPorCliente(
            @Parameter(description = "ID do cliente para consultar o histórico", required = true, example = "1")
            @PathVariable Long clienteId,
            HttpServletRequest httpRequest) {
        try {
            List<VendaResponseDTO> vendas = vendaService.listarVendasPorCliente(clienteId);
            return ResponseEntity.ok(vendas);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        }
    }
}

package materiamarcos.projetosecreto2.Controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    // Método helper para criar ErrorResponseDTO (você já tem este em outros controllers)
    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarVenda(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            VendaResponseDTO vendaCancelada = vendaService.cancelarVenda(id);
            return ResponseEntity.ok(vendaCancelada);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalStateException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Estado inválido", ex.getMessage(), httpRequest);
        } catch (EstoqueInsuficienteException ex) { // Embora seja estorno, o método de ajuste pode lançar
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de Estoque", ex.getMessage(), httpRequest);
        } catch (Exception ex) {

            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao cancelar a venda.", httpRequest);
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarNovaVenda(@Valid @RequestBody VendaRequestDTO vendaRequestDTO, HttpServletRequest httpRequest) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usernameBalconistaLogado = authentication.getName();

            if (usernameBalconistaLogado == null || usernameBalconistaLogado.equals("anonymousUser")) {

                return criarErrorResponse(HttpStatus.UNAUTHORIZED, "Não Autorizado", "Nenhum usuário autenticado para registrar a venda.", httpRequest);
            }

            if (vendaRequestDTO.getBalconistaId() == null) {
                return criarErrorResponse(HttpStatus.BAD_REQUEST, "Dados Inválidos", "ID do Balconista é obrigatório.", httpRequest);
            }
            if (vendaRequestDTO.getFarmaciaId() == null) {
                return criarErrorResponse(HttpStatus.BAD_REQUEST, "Dados Inválidos", "ID da Farmácia (filial da venda) é obrigatório.", httpRequest);
            }


            VendaResponseDTO vendaSalva = vendaService.registrarVenda(vendaRequestDTO, usernameBalconistaLogado);
            return new ResponseEntity<>(vendaSalva, HttpStatus.CREATED);

        } catch (EstoqueInsuficienteException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Estoque Insuficiente", ex.getMessage(), httpRequest);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Erro de integridade de dados ao registrar a venda: " + ex.getMessage(), httpRequest);
        } catch (Exception ex) {

            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao registrar a venda.", httpRequest);
        }
    }

    // TODO: Adicionar endpoints para:
    // - PUT /api/vendas/{id}/cancelar (Cancelar uma venda, se permitido pela regra de negócio)

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodasVendas() {
        List<VendaResponseDTO> vendas = vendaService.listarTodasVendas();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarVendaPorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            VendaResponseDTO venda = vendaService.buscarVendaPorId(id);
            return ResponseEntity.ok(venda);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao buscar a venda.", httpRequest);
        }
    }

    @GetMapping("/balconista/{balconistaId}")
    public ResponseEntity<?> listarVendasPorBalconista(@PathVariable Long balconistaId, HttpServletRequest httpRequest) {
        try {
            List<VendaResponseDTO> vendas = vendaService.listarVendasPorBalconista(balconistaId);
            return ResponseEntity.ok(vendas);
        } catch (EntityNotFoundException ex) { // Se o balconista não for encontrado
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao buscar vendas por balconista.", httpRequest);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarVendasPorCliente(@PathVariable Long clienteId, HttpServletRequest httpRequest) {
        try {
            List<VendaResponseDTO> vendas = vendaService.listarVendasPorCliente(clienteId);
            return ResponseEntity.ok(vendas);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao buscar vendas por cliente.", httpRequest);
        }
    }
}
package materiamarcos.projetosecreto2.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import materiamarcos.projetosecreto2.DTOs.EntradaEstoqueRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO; // Reutilize
import materiamarcos.projetosecreto2.DTOs.EstoqueResponseDTO;
import materiamarcos.projetosecreto2.Service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map; // Para um DTO de placeholder simples

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;


    private ResponseEntity<ErrorResponseDTO> criarRespostaDeErro(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @GetMapping
    public ResponseEntity<?> listarEstoque(HttpServletRequest httpRequest) {
        // TODO: Chamar o EstoqueService para buscar dados reais
        try {
            System.out.println("API GET /api/estoque chamada (placeholder)");
            // Simula uma resposta de sucesso com dados mocados
            List<Map<String, Object>> placeholderEstoque = Collections.singletonList(
                    Map.of("medicamentoId", 1, "nomeMedicamento", "Dipirona Placeholder", "quantidade", 100, "filial", "Principal")
            );
            return ResponseEntity.ok(placeholderEstoque);
        } catch (Exception e) {
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Não foi possível listar o estoque.", httpRequest);
        }
    }

    @PostMapping
    public ResponseEntity<?> adicionarAoEstoque(@RequestBody Map<String, Object> itemEstoqueDTO, HttpServletRequest httpRequest) { // Use um DTO real depois
        // TODO: Chamar o EstoqueService para adicionar item
        try {
            System.out.println("API POST /api/estoque chamada com dados: " + itemEstoqueDTO.toString());
            // Simula uma resposta de sucesso
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Placeholder: Item adicionado ao estoque", "itemRecebido", itemEstoqueDTO));
        } catch (Exception e) {
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Não foi possível adicionar ao estoque.", httpRequest);
        }
    }

    @GetMapping("/{medicamentoId}")
    public ResponseEntity<?> buscarEstoquePorMedicamento(@PathVariable Long medicamentoId, HttpServletRequest httpRequest) {
        // TODO: Chamar o EstoqueService
        try {
            System.out.println("API GET /api/estoque/" + medicamentoId + " chamada (placeholder)");
            return ResponseEntity.ok(Map.of("medicamentoId", medicamentoId, "quantidade", 50, "message", "Placeholder: Detalhes do estoque para o medicamento " + medicamentoId));
        } catch (Exception e) {
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Não foi possível buscar o estoque do medicamento.", httpRequest);
        }
    }

    @PostMapping("/entrada") // URL específica para entrada
    public ResponseEntity<?> registrarEntradaNoEstoque( // Este endpoint pode precisar ser mais específico, ex: /medicamento/{medicamentoId}/filial/{filialId}
            @Valid @RequestBody EntradaEstoqueRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {
            // Chama o método do serviço que você já implementou
            EstoqueResponseDTO estoqueAtualizado = estoqueService.registrarEntradaEstoque(requestDTO);
            return new ResponseEntity<>(estoqueAtualizado, HttpStatus.CREATED); // Retorna 201 com o estado do estoque
        } catch (EntityNotFoundException ex) {
            // Se o Medicamento ou Farmácia não forem encontrados pelo serviço
            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            // Para outros erros de lógica de negócio vindos do serviço
            return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            // Se houver algum problema de integridade no banco
            return criarRespostaDeErro(HttpStatus.CONFLICT, "Conflito de dados", "Erro de integridade de dados ao registrar entrada no estoque.", httpRequest);
        } catch (Exception ex) {
            // log.error("Erro ao registrar entrada no estoque: ", ex); // Boa prática logar
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao registrar entrada no estoque.", httpRequest);
        }
    }
}
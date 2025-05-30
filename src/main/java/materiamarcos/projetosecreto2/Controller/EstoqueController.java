package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO; // Reutilize
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

    // Helper para erro, pode ser movido para uma classe base de Controller ou ControllerAdvice
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
}
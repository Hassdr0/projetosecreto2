package materiamarcos.projetosecreto2.Controller;

import jakarta.persistence.EntityNotFoundException;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest; // Importe se já não estiver

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    // Método helper para criar ErrorResponseDTO
    private ResponseEntity<ErrorResponseDTO> criarErro(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI()), status);
    }

    @GetMapping
    public ResponseEntity<?> listarCompras(HttpServletRequest request) {
        // TODO: Chamar ComprasService.listarCompras()
        System.out.println("API GET /api/compras chamada (placeholder)");
        List<Map<String, Object>> placeholderCompras = Collections.singletonList(
                Map.of("id", 1, "data", "2025-05-29", "fornecedor", "Fornecedor ABC", "total", 250.99)
        );
        return ResponseEntity.ok(placeholderCompras);
    }

    @PostMapping
    public ResponseEntity<?> registrarNovaCompra(@RequestBody Map<String, Object> compraRequestDTO, HttpServletRequest request) {
        // TODO: Chamar ComprasService.registrarCompra(compraRequestDTO)
        try {
            System.out.println("API POST /api/compras chamada com dados: " + compraRequestDTO.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", 101, "message", "Placeholder: Compra registrada com sucesso", "dadosRecebidos", compraRequestDTO));
        } catch (Exception e) {
            // CORREÇÃO: Passar 'request' em vez de 'request.getRequestURI()'
            return criarErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Erro ao registrar compra.", request);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCompraPorId(@PathVariable Long id, HttpServletRequest request) {
        // TODO: Chamar ComprasService.buscarPorId(id)
        try {
            System.out.println("API GET /api/compras/" + id + " chamada (placeholder)");
            if (id == 1) {
                return ResponseEntity.ok(Map.of("id", id, "data", "2025-05-28", "fornecedor", "Fornecedor XYZ", "total", 500.00));
            } else {

                return criarErro(HttpStatus.NOT_FOUND, "Não Encontrado", "Compra com ID " + id + " não encontrada.", request);
            }
        } catch (Exception e) {

            return criarErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Erro ao buscar compra.", request);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCompra(@PathVariable Long id, @RequestBody Map<String, Object> compraRequestDTO, HttpServletRequest request) {
        // TODO: Chamar ComprasService.atualizarCompra(id, compraRequestDTO)
        try {
            System.out.println("API PUT /api/compras/" + id + " com dados: " + compraRequestDTO.toString());
            return ResponseEntity.ok(Map.of("id", id, "message", "Placeholder: Compra atualizada.", "dadosAtualizados", compraRequestDTO));
        } catch (Exception e) {

            return criarErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Erro ao atualizar compra.", request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCompra(@PathVariable Long id, HttpServletRequest request) {
        // TODO: Chamar ComprasService.deletarCompra(id)
        try {
            System.out.println("API DELETE /api/compras/" + id + " (placeholder)");
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                return criarErro(HttpStatus.NOT_FOUND, "Não Encontrado", e.getMessage(), request);
            }

            return criarErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Erro ao deletar compra.", request);
        }
    }
}
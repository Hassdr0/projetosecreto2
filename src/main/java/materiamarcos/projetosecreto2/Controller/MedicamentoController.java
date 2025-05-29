package materiamarcos.projetosecreto2.Controller; // Ajuste o pacote se necessário

import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.Service.MedicamentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Para @RestController, @RequestMapping

// Ainda não adicionei @PreAuthorize, fazer isso quando o JWT estiver validando
// import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/medicamentos") // Define o caminho base para todos os endpoints neste controller
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @PostMapping // Mapeia para POST em /api/medicamentos
    // @PreAuthorize("hasRole('ADMIN') or hasRole('FARMACEUTICO')") // Exemplo de como proteger depois
    public ResponseEntity<?> criarNovoMedicamento(@Valid @RequestBody MedicamentoRequestDTO requestDTO) {
        try {
            MedicamentoResponseDTO medicamentoSalvo = medicamentoService.criarMedicamento(requestDTO);
            return new ResponseEntity<>(medicamentoSalvo, HttpStatus.CREATED); // 201 Created
        } catch (EntityNotFoundException ex) {
            // Ex: PrincipioAtivo ou Industria não encontrados
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404 Not Found
        } catch (IllegalArgumentException ex) {
            // Ex: Código de barras duplicado
            return ResponseEntity.badRequest().body(ex.getMessage()); // 400 Bad Request
        } catch (Exception ex) {
            // Para outros erros inesperados
            // logar a exceção: log.error("Erro ao criar medicamento", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao criar medicamento: " + ex.getMessage());
        }
    }

    // Aqui virão os outros endpoints para Medicamentos (GET, PUT, DELETE)
}
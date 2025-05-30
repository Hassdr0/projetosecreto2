package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.Service.MedicamentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections; // Para lista vazia de placeholder
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    private ErrorResponseDTO criarErrorResponse(HttpStatus status, String errorMsg, String message, HttpServletRequest request) {
        return new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsg, message, request.getRequestURI());
    }

    @PostMapping
    public ResponseEntity<?> criarNovoMedicamento(@Valid @RequestBody MedicamentoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamentoSalvo = medicamentoService.criarMedicamento(requestDTO);
            return new ResponseEntity<>(medicamentoSalvo, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao criar o medicamento.", httpRequest), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listarTodosMedicamentos() {
        // A lógica completa está no MedicamentoService
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarTodos();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMedicamentoPorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamento = medicamentoService.buscarPorId(id);
            return ResponseEntity.ok(medicamento);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao buscar o medicamento.", httpRequest), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMedicamento(@PathVariable Long id, @Valid @RequestBody MedicamentoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            MedicamentoResponseDTO medicamentoAtualizado = medicamentoService.atualizarMedicamento(id, requestDTO);
            return ResponseEntity.ok(medicamentoAtualizado);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado ao atualizar o medicamento.", httpRequest), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMedicamento(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            medicamentoService.deletarMedicamento(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest), HttpStatus.NOT_FOUND);
        } catch (Exception ex) { // Ex: DataIntegrityViolationException
            return new ResponseEntity<>(criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Não foi possível deletar o medicamento.", httpRequest), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.BalconistaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.BalconistaResponseDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.Service.BalconistaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/balconistas")
public class BalconistaController {

    @Autowired
    private BalconistaService balconistaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @PostMapping
    public ResponseEntity<?> criarBalconista(@Valid @RequestBody BalconistaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO salvo = balconistaService.criarBalconista(requestDTO);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar balconista.", httpRequest);
        }
    }

    @GetMapping
    public ResponseEntity<List<BalconistaResponseDTO>> listarTodosBalconistas() {
        List<BalconistaResponseDTO> lista = balconistaService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarBalconistaPorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO dto = balconistaService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar balconista.", httpRequest);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarBalconista(@PathVariable Long id, @Valid @RequestBody BalconistaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            BalconistaResponseDTO atualizado = balconistaService.atualizarBalconista(id, requestDTO);
            return ResponseEntity.ok(atualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar balconista.", httpRequest);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarBalconista(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            balconistaService.deletarBalconista(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar balconista.", httpRequest);
        }
    }
}
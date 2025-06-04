package materiamarcos.projetosecreto2.Controller; // Ajuste o pacote

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaDTO; // Ou IndustriaResponseDTO
import materiamarcos.projetosecreto2.Service.IndustriaService;
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
@RequestMapping("/api/industrias")
public class IndustriaController {

    @Autowired
    private IndustriaService industriaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @PostMapping
    public ResponseEntity<?> criarIndustria(@Valid @RequestBody IndustriaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            IndustriaDTO salva = industriaService.criarIndustria(requestDTO);
            return new ResponseEntity<>(salva, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar indústria.", httpRequest);
        }
    }

    @GetMapping
    public ResponseEntity<List<IndustriaDTO>> listarTodas() {
        List<IndustriaDTO> lista = industriaService.listarTodas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            IndustriaDTO dto = industriaService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar indústria.", httpRequest);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarIndustria(@PathVariable Long id, @Valid @RequestBody IndustriaRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            IndustriaDTO atualizada = industriaService.atualizarIndustria(id, requestDTO);
            return ResponseEntity.ok(atualizada);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar indústria.", httpRequest);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarIndustria(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            industriaService.deletarIndustria(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), httpRequest);
        }
        catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar indústria.", httpRequest);
        }
    }
}
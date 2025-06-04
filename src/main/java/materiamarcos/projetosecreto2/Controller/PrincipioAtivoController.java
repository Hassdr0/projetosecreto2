package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
import materiamarcos.projetosecreto2.Service.PrincipioAtivoService;
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
@RequestMapping("/api/principios-ativos")
public class PrincipioAtivoController {

    @Autowired
    private PrincipioAtivoService principioAtivoService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @PostMapping
    public ResponseEntity<?> criarPrincipioAtivo(@Valid @RequestBody PrincipioAtivoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO salvo = principioAtivoService.criarPrincipioAtivo(requestDTO);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar princípio ativo.", httpRequest);
        }
    }

    @GetMapping
    public ResponseEntity<List<PrincipioAtivoResponseDTO>> listarTodos() {
        List<PrincipioAtivoResponseDTO> lista = principioAtivoService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO dto = principioAtivoService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar princípio ativo.", httpRequest);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPrincipioAtivo(@PathVariable Long id, @Valid @RequestBody PrincipioAtivoRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            PrincipioAtivoResponseDTO atualizado = principioAtivoService.atualizarPrincipioAtivo(id, requestDTO);
            return ResponseEntity.ok(atualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar princípio ativo.", httpRequest);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPrincipioAtivo(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            principioAtivoService.deletarPrincipioAtivo(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Não foi possível deletar: Princípio Ativo está em uso.", httpRequest);
        }
        catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar princípio ativo.", httpRequest);
        }
    }
}
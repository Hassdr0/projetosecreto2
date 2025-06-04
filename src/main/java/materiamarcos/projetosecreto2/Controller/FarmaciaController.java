package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaResponseDTO;
import materiamarcos.projetosecreto2.Service.FarmaciaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/filiais") //api/filiais como no seu menu
public class FarmaciaController {

    @Autowired
    private FarmaciaService farmaciaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI()), status);
    }

    @PostMapping
    public ResponseEntity<?> criarFilial(@Valid @RequestBody FarmaciaRequestDTO dto, HttpServletRequest request) {
        try {
            return new ResponseEntity<>(farmaciaService.criar(dto), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar filial.", request);
        }
    }

    @GetMapping
    public ResponseEntity<List<FarmaciaResponseDTO>> listarFiliais() {
        return ResponseEntity.ok(farmaciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarFilialPorId(@PathVariable Long id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(farmaciaService.buscarPorId(id));
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFilial(@PathVariable Long id, @Valid @RequestBody FarmaciaRequestDTO dto, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(farmaciaService.atualizar(id, dto));
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFilial(@PathVariable Long id, HttpServletRequest request) {
        try {
            farmaciaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        }
        // Adicionar catch para DataIntegrityViolationException se implementado no service
    }
}
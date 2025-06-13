package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaResponseDTO;
import materiamarcos.projetosecreto2.Service.FarmaciaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/filiais")
public class FilialController {

    @Autowired
    private FarmaciaService farmaciaService;

    // Método helper para criar respostas de erro padronizadas
    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                new Date().getTime(),
                status.value(),
                errorMsgKey,
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Apenas um ADMIN pode criar uma nova filial
    public ResponseEntity<?> criarFilial(@Valid @RequestBody FarmaciaRequestDTO dto, HttpServletRequest request) {
        try {
            FarmaciaResponseDTO novaFilial = farmaciaService.criar(dto);
            return new ResponseEntity<>(novaFilial, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar filial.", request);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Qualquer usuário autenticado pode listar as filiais
    public ResponseEntity<List<FarmaciaResponseDTO>> listarFiliais() {
        List<FarmaciaResponseDTO> filiais = farmaciaService.listarTodas();
        return ResponseEntity.ok(filiais);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Qualquer usuário autenticado pode ver detalhes de uma filial
    public ResponseEntity<?> buscarFilialPorId(@PathVariable Long id, HttpServletRequest request) {
        try {
            FarmaciaResponseDTO filial = farmaciaService.buscarPorId(id);
            return ResponseEntity.ok(filial);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas um ADMIN pode atualizar uma filial
    public ResponseEntity<?> atualizarFilial(@PathVariable Long id, @Valid @RequestBody FarmaciaRequestDTO dto, HttpServletRequest request) {
        try {
            FarmaciaResponseDTO filialAtualizada = farmaciaService.atualizar(id, dto);
            return ResponseEntity.ok(filialAtualizada);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas um ADMIN pode deletar uma filial
    public ResponseEntity<?> deletarFilial(@PathVariable Long id, HttpServletRequest request) {
        try {
            farmaciaService.deletar(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        } catch (DataIntegrityViolationException ex) {
            // Este erro ocorre se a filial estiver em uso (ex: tem estoque associado)
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", "Não é possível deletar a filial pois ela está em uso.", request);
        }
    }
}
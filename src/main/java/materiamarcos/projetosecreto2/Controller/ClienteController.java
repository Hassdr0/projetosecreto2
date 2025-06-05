package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.ClienteRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ClienteResponseDTO;
import materiamarcos.projetosecreto2.DTOs.ErrorResponseDTO;
import materiamarcos.projetosecreto2.Service.ClienteService;
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
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @PostMapping
    public ResponseEntity<?> criarCliente(@Valid @RequestBody ClienteRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            ClienteResponseDTO salvo = clienteService.criarCliente(requestDTO);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar cliente.", httpRequest);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodosClientes() {
        List<ClienteResponseDTO> lista = clienteService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            ClienteResponseDTO dto = clienteService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao buscar cliente.", httpRequest);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO requestDTO, HttpServletRequest httpRequest) {
        try {
            ClienteResponseDTO atualizado = clienteService.atualizarCliente(id, requestDTO);
            return ResponseEntity.ok(atualizado);
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao atualizar cliente.", httpRequest);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCliente(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {
            return criarErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao deletar cliente.", httpRequest);
        }
    }
}
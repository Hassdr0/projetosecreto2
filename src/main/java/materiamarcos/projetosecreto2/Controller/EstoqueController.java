package materiamarcos.projetosecreto2.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import materiamarcos.projetosecreto2.DTOs.*;
import materiamarcos.projetosecreto2.Service.EstoqueService;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import materiamarcos.projetosecreto2.DTOs.EstoqueResponseDTO;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;



    private ResponseEntity<ErrorResponseDTO> criarRespostaDeErro(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @GetMapping
    public ResponseEntity<?> listarEstoque() {
        try {
            List<EstoqueResponseDTO> todoEstoque = estoqueService.listarTodoEstoque();
            if (todoEstoque.isEmpty()) {

                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(todoEstoque); // Retorna 200 OK com a lista de estoque
        } catch (Exception ex) {

            HttpServletRequest request = null;
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Não foi possível listar o estoque.", request);
        }
    }

    @PostMapping
    public ResponseEntity<?> adicionarAoEstoque(@RequestBody Map<String, Object> itemEstoqueDTO, HttpServletRequest httpRequest) { // Use um DTO real depois

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

        try {
            System.out.println("API GET /api/estoque/" + medicamentoId + " chamada (placeholder)");
            return ResponseEntity.ok(Map.of("medicamentoId", medicamentoId, "quantidade", 50, "message", "Placeholder: Detalhes do estoque para o medicamento " + medicamentoId));
        } catch (Exception e) {
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Não foi possível buscar o estoque do medicamento.", httpRequest);
        }
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntradaNoEstoque(
            @Valid @RequestBody EntradaEstoqueRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {

            EstoqueResponseDTO estoqueAtualizado = estoqueService.registrarEntradaEstoque(requestDTO);
            return new ResponseEntity<>(estoqueAtualizado, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {

            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {

            return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (DataIntegrityViolationException ex) {

            return criarRespostaDeErro(HttpStatus.CONFLICT, "Conflito de dados", "Erro de integridade de dados ao registrar entrada no estoque.", httpRequest);
        } catch (Exception ex) {

            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao registrar entrada no estoque.", httpRequest);
        }
    }
    //LISTAR TODO O ESTOQUE DE UMA FILIAL

    @GetMapping("/filial/{farmaciaId}")
    public ResponseEntity<?> listarEstoquePorFarmacia(
            @PathVariable Long farmaciaId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorFarmacia(farmaciaId);
            if (estoques.isEmpty()) {

                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) {
            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {

            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao listar o estoque da filial.", httpRequest);
        }
    }
    //BUSCAR ESTOQUE DE UM MEDICAMENTO ESPECÍFICO EM UMA FILIAL ESPECÍFICA

    @GetMapping("/medicamento/{medicamentoId}/filial/{farmaciaId}")
    public ResponseEntity<?> buscarEstoquePorMedicamentoEFarmacia(
            @PathVariable Long medicamentoId,
            @PathVariable Long farmaciaId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorMedicamentoEFarmacia(medicamentoId, farmaciaId);

            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) {
            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {

            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao buscar o estoque.", httpRequest);
        }
    }

    @GetMapping("/medicamento/{medicamentoId}")
    public ResponseEntity<?> listarEstoquePorMedicamento(
            @PathVariable Long medicamentoId,
            HttpServletRequest httpRequest) {
        try {
            List<EstoqueResponseDTO> estoques = estoqueService.consultarEstoquePorMedicamento(medicamentoId);
            if (estoques.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(estoques);
        } catch (EntityNotFoundException ex) { // Se o Medicamento não for encontrado
            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            // log.error("Erro ao listar estoque por medicamento: {}", medicamentoId, ex);
            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao listar o estoque do medicamento.", httpRequest);
        }
    }

    //AJUSTE MANUAL DE ESTOQUE (incluindo baixa)
    @PostMapping("/ajuste")
    public ResponseEntity<?> ajustarEstoque(
            @Valid @RequestBody AjusteEstoqueRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        try {

            if (requestDTO.getQuantidadeAjuste() < 0) {

                if (requestDTO.getMedicamentoId() == null || requestDTO.getFarmaciaId() == null) {
                    return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Dados incompletos", "MedicamentoID e FarmaciaID são obrigatórios para baixa.", httpRequest);
                }
                estoqueService.darBaixaEstoque(
                        requestDTO.getMedicamentoId(),
                        requestDTO.getFarmaciaId(),
                        requestDTO.getLote(),
                        Math.abs(requestDTO.getQuantidadeAjuste())
                );
                return ResponseEntity.ok().body("Baixa de estoque realizada com sucesso. Motivo: " + requestDTO.getMotivoAjuste());

            } else if (requestDTO.getQuantidadeAjuste() > 0) {

                return criarRespostaDeErro(HttpStatus.NOT_IMPLEMENTED, "Não implementado", "Ajuste de entrada manual ainda não implementado por este endpoint.", httpRequest);
            } else {
                return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Dados inválidos", "Quantidade para ajuste não pode ser zero.", httpRequest);
            }

        } catch (EstoqueInsuficienteException ex) {
            return criarRespostaDeErro(HttpStatus.CONFLICT, "Estoque insuficiente", ex.getMessage(), httpRequest);
        } catch (EntityNotFoundException ex) {
            return criarRespostaDeErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), httpRequest);
        } catch (IllegalArgumentException ex) {
            return criarRespostaDeErro(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), httpRequest);
        } catch (Exception ex) {

            return criarRespostaDeErro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno", "Ocorreu um erro inesperado ao ajustar o estoque.", httpRequest);
        }
    }
    @GetMapping("/alertas-minimo")
    public ResponseEntity<List<AlertaEstoqueDTO>> getAlertasEstoqueMinimo() {
        List<AlertaEstoqueDTO> alertas = estoqueService.verificarAlertasEstoqueMinimo();
        if (alertas.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 se não houver alertas
        }
        return ResponseEntity.ok(alertas);
    }
}
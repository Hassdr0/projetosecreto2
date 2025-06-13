package materiamarcos.projetosecreto2.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = " Filiais / Farmácias", description = "Endpoints para gerenciamento das filiais da rede de farmácias")
@RestController
@RequestMapping("/api/filiais")
@SecurityRequirement(name = "bearerAuth") // Aplica o cadeado (autenticação JWT) a todos os endpoints desta classe
public class FarmaciaController {

    @Autowired
    private FarmaciaService farmaciaService;

    private ResponseEntity<ErrorResponseDTO> criarErrorResponse(HttpStatus status, String errorMsgKey, String message, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(new Date().getTime(), status.value(), errorMsgKey, message, request.getRequestURI()), status);
    }

    @Operation(summary = "Criar uma nova filial",
            description = "Registra uma nova farmácia/filial no sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Filial criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FarmaciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome/CNPJ já existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarFilial(@Valid @RequestBody FarmaciaRequestDTO dto, HttpServletRequest request) {
        try {
            return new ResponseEntity<>(farmaciaService.criar(dto), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        } catch (Exception ex) {
            return criarErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Erro ao criar filial.", request);
        }
    }

    @Operation(summary = "Listar todas as filiais",
            description = "Retorna uma lista de todas as farmácias/filiais cadastradas. Requer permissão de ADMIN ou BALCONISTA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de filiais retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FarmaciaResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BALCONISTA')")
    public ResponseEntity<List<FarmaciaResponseDTO>> listarFiliais() {
        return ResponseEntity.ok(farmaciaService.listarTodas());
    }

    @Operation(summary = "Buscar uma filial por ID",
            description = "Retorna os detalhes de uma filial específica com base no seu ID. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filial encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FarmaciaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Filial não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarFilialPorId(
            @Parameter(description = "ID da filial a ser buscada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            return ResponseEntity.ok(farmaciaService.buscarPorId(id));
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        }
    }

    @Operation(summary = "Atualizar uma filial existente",
            description = "Atualiza os dados de uma farmácia/filial específica. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filial atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FarmaciaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Filial não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome/CNPJ já existe em outra filial")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarFilial(
            @Parameter(description = "ID da filial a ser atualizada", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody FarmaciaRequestDTO dto,
            HttpServletRequest request) {
        try {
            return ResponseEntity.ok(farmaciaService.atualizar(id, dto));
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        } catch (IllegalArgumentException ex) {
            return criarErrorResponse(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage(), request);
        }
    }

    @Operation(summary = "Deletar uma filial",
            description = "Remove uma farmácia/filial do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Filial deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Filial não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito - A filial não pode ser deletada pois está em uso")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarFilial(
            @Parameter(description = "ID da filial a ser deletada", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            farmaciaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return criarErrorResponse(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage(), request);
        }
        // Adicionar catch para DataIntegrityViolationException se implementado no service
    }
}

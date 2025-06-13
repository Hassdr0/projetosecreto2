package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO para registar a entrada de um item no stock.")
public class EntradaEstoqueRequestDTO {

    @NotNull(message = "ID do Medicamento é obrigatório")
    @Schema(description = "ID do medicamento que está a entrar no stock.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medicamentoId;

    @NotNull(message = "ID da Farmácia é obrigatório")
    @Schema(description = "ID da farmácia/filial onde o stock está a ser registado.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long farmaciaId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    @Schema(description = "Quantidade de unidades que estão a entrar no stock.", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantidade;

    @Size(max = 100, message = "Lote deve ter no máximo 100 caracteres")
    @Schema(description = "Código do lote do produto (opcional).", example = "LOTE-AB123")
    private String lote;

    @Schema(description = "Data de validade específica deste lote (formato AAAA-MM-DD).", example = "2027-12-31")
    private LocalDate dataDeValidadeDoLote;

    @Min(value = 0, message = "Preço de custo do lote não pode ser negativo")
    @Schema(description = "Preço de custo unitário para este lote específico (opcional).", example = "5.75")
    private BigDecimal precoDeCustoDoLote;
}

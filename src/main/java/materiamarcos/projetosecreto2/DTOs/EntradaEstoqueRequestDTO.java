package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EntradaEstoqueRequestDTO {

    @NotNull(message = "ID do Medicamento é obrigatório")
    private Long medicamentoId;

    @NotNull(message = "ID da Farmácia é obrigatório")
    private Long farmaciaId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    @Size(max = 100, message = "Lote deve ter no máximo 100 caracteres")
    private String lote;

    @FutureOrPresent(message = "Data de validade do lote deve ser no presente ou futuro")
    private LocalDate dataDeValidadeDoLote;
    @Min(value = 0, message = "Preço de custo do lote não pode ser negativo")
    private BigDecimal precoDeCustoDoLote;
}
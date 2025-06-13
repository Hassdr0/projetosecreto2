package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um registo de stock em respostas da API.")
public class EstoqueResponseDTO {

    @Schema(description = "ID único do registo de stock.")
    private Long id;

    @Schema(description = "Detalhes do medicamento em stock.")
    private MedicamentoResponseDTO medicamento;

    @Schema(description = "Detalhes da farmácia onde o stock está localizado.")
    private FarmaciaResponseDTO farmacia;

    @Schema(description = "Quantidade de unidades do item em stock.")
    private Integer quantidade;

    @Schema(description = "Código do lote do item em stock.")
    private String lote;

    @Schema(description = "Data de validade específica deste lote.")
    private LocalDate dataDeValidadeDoLote;

    @Schema(description = "Preço de custo unitário para este lote específico.")
    private BigDecimal precoDeCustoDoLote;

    @Schema(description = "Data e hora da última atualização deste registo de stock.")
    private LocalDateTime dataUltimaAtualizacao;
}

package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de uma cotação em respostas da API.")
public class CotacaoResponseDTO {

    @Schema(description = "ID único da cotação.")
    private Long id;

    @Schema(description = "ID do medicamento cotado.")
    private Long medicamentoId;

    @Schema(description = "Nome do medicamento cotado.")
    private String nomeMedicamento;

    @Schema(description = "ID da indústria que forneceu a cotação.")
    private Long industriaId;

    @Schema(description = "Nome da indústria.")
    private String nomeIndustria;

    @Schema(description = "Preço unitário do medicamento nesta cotação.")
    private BigDecimal precoUnitarioCotado;

    @Schema(description = "Data em que a cotação foi registrada.")
    private LocalDate dataCotacao;

    @Schema(description = "Indica se a cotação ainda é considerada válida.")
    private boolean ativa;
}

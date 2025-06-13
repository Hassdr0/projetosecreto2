package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de um balconista em respostas da API.")
public class BalconistaResponseDTO {

    @Schema(description = "ID único do balconista.", example = "1")
    private Long id;

    @Schema(description = "Nome completo do balconista.", example = "João Atendente")
    private String nome;

    @Schema(description = "CPF do balconista.", example = "111.222.333-44")
    private String cpf;

    @Schema(description = "Taxa de comissão como um decimal.", example = "0.05")
    private BigDecimal taxaComissao;

    @Schema(description = "Salário base do balconista.", example = "1500.00")
    private BigDecimal salarioBase;
}

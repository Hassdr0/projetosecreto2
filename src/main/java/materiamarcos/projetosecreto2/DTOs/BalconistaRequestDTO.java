package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para criar ou atualizar um balconista.")
public class BalconistaRequestDTO {

    @NotBlank(message = "Nome do balconista é obrigatório")
    @Size(min = 3, max = 100)
    @Schema(description = "Nome completo do balconista.", example = "João Atendente", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Size(max = 14)
    @Schema(description = "CPF do balconista (formato com máscara).", example = "111.222.333-44")
    private String cpf;

    @NotNull(message = "Taxa de comissão é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    @Digits(integer = 1, fraction = 4)
    @Schema(description = "Taxa de comissão como um decimal (ex: 0.05 para 5%).", example = "0.05", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal taxaComissao;

    @DecimalMin(value = "0.01")
    @Digits(integer = 8, fraction = 2)
    @Schema(description = "Salário base do balconista (opcional).", example = "1500.00")
    private BigDecimal salarioBase;
}

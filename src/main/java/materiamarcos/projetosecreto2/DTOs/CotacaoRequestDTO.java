package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para registrar uma nova cotação de preço para um medicamento.")
public class CotacaoRequestDTO {

    @NotNull
    @Schema(description = "ID do medicamento que está sendo cotado.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medicamentoId;

    @NotNull
    @Schema(description = "ID da indústria/fornecedor que forneceu a cotação.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long industriaId;

    @NotNull
    @Positive
    @Schema(description = "Preço unitário do medicamento na cotação.", example = "5.25", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precoUnitarioCotado;
}

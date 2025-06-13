package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para representar um item dentro de uma nova requisição de venda.")
public class ItemVendaRequestDTO {

    @NotNull(message = "ID do Medicamento é obrigatório")
    @Schema(description = "ID do medicamento a ser vendido.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medicamentoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    @Schema(description = "Quantidade de unidades do medicamento a ser vendido.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantidade;
}

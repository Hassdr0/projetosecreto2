package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para iniciar um pedido de compra para um medicamento. O sistema buscará a melhor cotação.")
public class CompraRequestDTO {

    @NotNull
    @Schema(description = "ID do medicamento a ser comprado.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medicamentoId;

    @NotNull
    @Min(1)
    @Schema(description = "Quantidade desejada do medicamento.", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantidade;
}

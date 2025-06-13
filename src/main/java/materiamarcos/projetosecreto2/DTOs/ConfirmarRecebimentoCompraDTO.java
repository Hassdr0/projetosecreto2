package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para confirmar o recebimento de uma compra, informando a nota fiscal.")
public class ConfirmarRecebimentoCompraDTO {

    @NotBlank
    @Schema(description = "Número da nota fiscal recebida do fornecedor.", example = "NF-0012345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String numeroNotaFiscal;

    // Futuramente, pode-se adicionar uma lista de lotes e validades aqui se eles não vieram no pedido de compra original
}

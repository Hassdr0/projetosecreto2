package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um item de uma venda em respostas da API.")
public class ItemVendaResponseDTO {

    @Schema(description = "ID único do item da venda.")
    private Long id;

    @Schema(description = "ID do medicamento vendido.")
    private Long medicamentoId;

    @Schema(description = "Nome do medicamento vendido.")
    private String nomeMedicamento;

    @Schema(description = "Quantidade de unidades vendidas.")
    private Integer quantidade;

    @Schema(description = "Preço unitário do medicamento no momento da venda.")
    private BigDecimal precoUnitarioVenda;

    @Schema(description = "Subtotal para este item (quantidade * preço unitário).")
    private BigDecimal subtotalItem;
}

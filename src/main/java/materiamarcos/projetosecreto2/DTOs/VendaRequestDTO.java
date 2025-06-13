package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO para registrar uma nova venda.")
public class VendaRequestDTO {

    @Schema(description = "ID do cliente associado à venda (opcional, para vendas a clientes anônimos).", example = "1")
    private Long clienteId;

    @NotNull(message = "ID do Balconista é obrigatório")
    @Schema(description = "ID do balconista que realizou a venda.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long balconistaId;

    @NotNull(message = "ID da Farmácia (filial da venda) é obrigatório")
    @Schema(description = "ID da farmácia/filial onde a venda foi realizada.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long farmaciaId;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Size(max = 50)
    @Schema(description = "Método de pagamento utilizado.", example = "Cartão de Crédito", requiredMode = Schema.RequiredMode.REQUIRED)
    private String formaPagamento;

    @Size(max = 255)
    @Schema(description = "Observações adicionais sobre a venda (opcional).", example = "Cliente pediu para embrulhar para presente.")
    private String observacoes;

    @Size(max = 100)
    @Schema(description = "Nome do motoboy para tele-entrega (opcional).", example = "Carlos Entregas")
    private String motoboyEntrega;

    @Size(max = 255)
    @Schema(description = "Endereço de entrega para tele-entrega (opcional).", example = "Rua das Entregas, 999")
    private String enderecoEntrega;

    @NotEmpty(message = "Lista de itens não pode ser vazia")
    @Valid
    @Schema(description = "Lista dos itens que estão sendo vendidos.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ItemVendaRequestDTO> itens;
}

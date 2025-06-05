package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemVendaRequestDTO {
    @NotNull(message = "ID do Medicamento é obrigatório")
    private Long medicamentoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    // O preço unitário será buscado do Medicamento no momento da venda pelo serviço
    // Mas em alguns cenários, pode-se permitir que ele seja enviado (ex: desconto manual)
    // O serviço buscará do cadastro de Medicamento
}
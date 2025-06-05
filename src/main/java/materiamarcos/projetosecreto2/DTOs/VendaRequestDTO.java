package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VendaRequestDTO {

    private Long clienteId;

    @NotNull(message = "ID do Balconista é obrigatório")
    private Long balconistaId;

    @NotNull(message = "ID da Farmácia (filial da venda) é obrigatório")
    private Long farmaciaId;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Size(max = 50)
    private String formaPagamento;

    @Size(max = 255)
    private String observacoes;

    @Size(max = 100)
    private String motoboyEntrega;

    @Size(max = 255)
    private String enderecoEntrega;

    @NotEmpty(message = "Lista de itens não pode ser vazia")
    @Valid
    private List<ItemVendaRequestDTO> itens;
}
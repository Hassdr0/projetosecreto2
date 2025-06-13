package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados completos de uma venda em respostas da API.")
public class VendaResponseDTO {

    @Schema(description = "ID único da venda.")
    private Long id;

    @Schema(description = "Data e hora em que a venda foi registrada.")
    private LocalDateTime dataVenda;

    @Schema(description = "Valor total da venda.")
    private BigDecimal valorTotal;

    @Schema(description = "Método de pagamento utilizado.")
    private String formaPagamento;

    @Schema(description = "Status atual da venda (ex: CONCLUIDA, CANCELADA).")
    private String statusVenda;

    @Schema(description = "Observações adicionais sobre a venda.")
    private String observacoes;

    @Schema(description = "Detalhes do cliente associado (pode ser nulo).")
    private ClienteResponseDTO cliente;

    @Schema(description = "Detalhes do balconista que realizou a venda.")
    private BalconistaResponseDTO balconista;

    @Schema(description = "Detalhes da farmácia onde a venda ocorreu.")
    private FarmaciaResponseDTO farmacia;

    @Schema(description = "Nome do motoboy para tele-entrega.")
    private String motoboyEntrega;

    @Schema(description = "Endereço de entrega para tele-entrega.")
    private String enderecoEntrega;

    @Schema(description = "Número da nota fiscal gerada para a venda.")
    private String numeroNotaFiscal;

    @Schema(description = "Lista de todos os itens vendidos nesta transação.")
    private List<ItemVendaResponseDTO> itens;
}

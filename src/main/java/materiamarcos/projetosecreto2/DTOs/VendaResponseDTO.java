package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseDTO {
    private Long id;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private String formaPagamento;
    private String statusVenda;
    private String observacoes;

    private ClienteResponseDTO cliente;
    private BalconistaResponseDTO balconista;
    private FarmaciaResponseDTO farmacia;

    private String motoboyEntrega;
    private String enderecoEntrega;
    private String numeroNotaFiscal;

    private List<ItemVendaResponseDTO> itens;
}
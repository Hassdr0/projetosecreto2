package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaResponseDTO {
    private Long id;
    private Long medicamentoId;
    private String nomeMedicamento;
    private Integer quantidade;
    private BigDecimal precoUnitarioVenda;
    private BigDecimal subtotalItem;
}
// Em materiamarcos.projetosecreto2.DTOs
package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalconistaResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private BigDecimal taxaComissao;
    private BigDecimal salarioBase;
}
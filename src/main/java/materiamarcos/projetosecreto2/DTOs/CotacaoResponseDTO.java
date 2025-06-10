package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoResponseDTO {
    private Long id;
    private Long medicamentoId;
    private String nomeMedicamento;
    private Long industriaId;
    private String nomeIndustria;
    private BigDecimal precoUnitarioCotado;
    private LocalDate dataCotacao;
    private boolean ativa;
}
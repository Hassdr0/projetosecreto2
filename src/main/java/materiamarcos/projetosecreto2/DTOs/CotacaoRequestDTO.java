package materiamarcos.projetosecreto2.DTOs;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CotacaoRequestDTO {
    @NotNull private Long medicamentoId;
    @NotNull private Long industriaId;
    @NotNull @Positive private BigDecimal precoUnitarioCotado;
}
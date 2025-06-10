package materiamarcos.projetosecreto2.DTOs;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompraRequestDTO {
    @NotNull private Long medicamentoId;
    @NotNull @Min(1) private Integer quantidade;
}
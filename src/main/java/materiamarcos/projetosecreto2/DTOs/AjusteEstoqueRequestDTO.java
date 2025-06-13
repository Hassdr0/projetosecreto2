package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO para realizar um ajuste manual no estoque de um item.")
public class AjusteEstoqueRequestDTO {

    @NotNull(message = "Quantidade para ajuste é obrigatória")
    @Schema(description = "A quantidade a ser ajustada. Positivo para entrada, negativo para saída/baixa.", example = "-5")
    private Integer quantidadeAjuste;

    @NotNull(message = "ID do Medicamento é obrigatório")
    @Schema(description = "ID do medicamento cujo estoque será ajustado.", example = "1")
    private Long medicamentoId;

    @NotNull(message = "ID da Farmácia é obrigatório")
    @Schema(description = "ID da farmácia onde o ajuste de estoque ocorrerá.", example = "1")
    private Long farmaciaId;

    @Schema(description = "Lote específico a ser ajustado (opcional).", example = "LOTE_TESTE_001")
    private String lote;

    @Schema(description = "Motivo do ajuste manual para fins de auditoria.", example = "Perda por avaria.")
    private String motivoAjuste;
}

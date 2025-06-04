package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjusteEstoqueRequestDTO {
    @NotNull(message = "Quantidade para ajuste é obrigatória")
    private Integer quantidadeAjuste; // Pode ser positivo (entrada) ou negativo (saída/perda)

    @NotNull(message = "ID do Medicamento é obrigatório")
    private Long medicamentoId;

    @NotNull(message = "ID da Farmácia é obrigatório")
    private Long farmaciaId;

    private String lote; // Opcional, se o ajuste for para um lote específico

    private String motivoAjuste; // Ex: "Perda por avaria", "Contagem de inventário"
}
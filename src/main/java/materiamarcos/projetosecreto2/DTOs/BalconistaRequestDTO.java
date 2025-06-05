// Em materiamarcos.projetosecreto2.DTOs
package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalconistaRequestDTO {
    @NotBlank(message = "Nome do balconista é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Size(max = 14)
    private String cpf;

    @NotNull(message = "Taxa de comissão é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de comissão não pode ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "Taxa de comissão deve ser entre 0.0 e 1.0 (ex: 0.05 para 5%)")
    @Digits(integer = 1, fraction = 4, message = "Formato inválido para taxa de comissão. Use até 4 casas decimais (ex: 0.055 para 5.5%)")
    private BigDecimal taxaComissao;

    @DecimalMin(value = "0.01", message = "Salário base deve ser maior que zero, se informado")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido para salário base")
    private BigDecimal salarioBase;
}
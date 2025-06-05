
package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "balconistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balconista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do balconista é obrigatório")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Column(unique = true, length = 14)
    private String cpf;

    @NotNull(message = "Taxa de comissão é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de comissão não pode ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "Taxa de comissão deve ser entre 0.0 e 1.0 (ex: 0.05 para 5%)")
    @Digits(integer = 1, fraction = 4, message = "Formato inválido para taxa de comissão")
    @Column(name = "taxa_comissao", precision = 5, scale = 4, nullable = false)
    private BigDecimal taxaComissao;

    @DecimalMin(value = "0.01", message = "Salário base deve ser maior que zero, se informado")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido para salário base")
    @Column(name = "salario_base", precision = 10, scale = 2)
    private BigDecimal salarioBase; // Opcional


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;
}
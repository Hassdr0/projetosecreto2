package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industria_id", nullable = false)
    private Industria industria;

    @NotNull
    @Positive
    @Column(name = "preco_unitario_cotado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioCotado;

    @NotNull
    @Column(name = "data_cotacao", nullable = false)
    private LocalDate dataCotacao;

    private boolean ativa = true; // Para saber se esta cotação ainda é válida
}
package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque", uniqueConstraints = {
        // Garante que não haja múltiplas entradas para o mesmo medicamento, na mesma farmácia, com o mesmo lote.
        // Se você não for controlar por lote, pode remover 'lote' da constraint.
        @UniqueConstraint(columnNames = {"medicamento_id", "farmacia_id", "lote"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Medicamento é obrigatório para o registro de estoque")
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Um registro de estoque DEVE ter um medicamento
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotNull(message = "Farmácia é obrigatória para o registro de estoque")
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Um registro de estoque DEVE pertencer a uma farmácia
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 100)
    private String lote;

    @Column(name = "data_validade_lote")
    private LocalDate dataDeValidadeDoLote; // Validade específica deste lote

    @Column(name = "preco_custo_lote", precision = 10, scale = 2)
    private BigDecimal precoDeCustoDoLote; // Custo deste lote específico

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

}
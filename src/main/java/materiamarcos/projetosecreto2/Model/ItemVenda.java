package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Venda é obrigatória para o item")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull(message = "Medicamento é obrigatório para o item da venda")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "Preço unitário no momento da venda é obrigatório")
    @Positive(message = "Preço unitário deve ser positivo")
    @Column(name = "preco_unitario_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioVenda;
    @NotNull(message = "Subtotal do item é obrigatório")
    @PositiveOrZero(message = "Subtotal do item não pode ser negativo")
    @Column(name = "subtotal_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalItem; // Calculado: quantidade * precoUnitarioVenda

    public ItemVenda(Medicamento medicamento, Integer quantidade, BigDecimal precoUnitarioVenda) {
        this.medicamento = medicamento;
        this.quantidade = quantidade;
        this.precoUnitarioVenda = precoUnitarioVenda;
        this.subtotalItem = precoUnitarioVenda.multiply(new BigDecimal(quantidade));
    }

    public void recalcularSubtotal() {
        if (this.quantidade != null && this.precoUnitarioVenda != null) {
            this.subtotalItem = this.precoUnitarioVenda.multiply(new BigDecimal(this.quantidade));
        }
    }
}
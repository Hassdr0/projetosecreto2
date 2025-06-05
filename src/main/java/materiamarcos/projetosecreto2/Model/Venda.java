package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data da venda é obrigatória")
    @PastOrPresent(message = "Data da venda não pode ser no futuro")
    @Column(nullable = false)
    private LocalDateTime dataVenda;

    @NotNull(message = "Valor total da venda é obrigatório")
    @PositiveOrZero(message = "Valor total não pode ser negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Column(nullable = false, length = 50)
    private String formaPagamento;

    @Column(length = 50)
    private String statusVenda;

    @Column(length = 100)
    private String observacoes;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull(message = "Balconista é obrigatório para a venda")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "balconista_id", nullable = false)
    private Balconista balconista;

    @NotNull(message = "Farmácia (filial) é obrigatória para a venda")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia; // Para saber de qual filial foi a venda e onde dar baixa no estoque

    // Uma venda tem muitos itens de venda
    // CascadeType.ALL: Se uma venda for deletada, seus itens também serão.
    // orphanRemoval = true: Se um ItemVenda for removido da lista 'itens' e a Venda for salva, o ItemVenda será deletado do banco.

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(name = "motoboy_entrega", length = 100)
    private String motoboyEntrega;

    @Column(name = "endereco_entrega", length = 255)
    private String enderecoEntrega;

    @Column(name = "numero_nota_fiscal", length = 50)
    private String numeroNotaFiscal;

    public void addItem(ItemVenda item) {
        itens.add(item);
        item.setVenda(this);
    }

    public void removeItem(ItemVenda item) {
        itens.remove(item);
        item.setVenda(null);
    }

    @PrePersist
    protected void onCreate() {
        dataVenda = LocalDateTime.now();
        if (statusVenda == null) {
            statusVenda = "CONCLUIDA";
        }
    }
}
package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "codigo_de_barras", length = 50, unique = true)
    private String codigoDeBarras; //mas bom ter

    private LocalDate validade;

    @Column(name = "preco_compra", precision = 10, scale = 2)
    private BigDecimal precoCompra;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "promocao")
    private boolean promocao = false;

    @Column(name = "preco_promocional", precision = 10, scale = 2)
    private BigDecimal precoPromocional;

    @ManyToOne(fetch = FetchType.EAGER) // Carrega o princípio ativo
    @JoinColumn(name = "principio_ativo_id", nullable = false)
    private PrincipioAtivo principioAtivo;

    @ManyToOne(fetch = FetchType.EAGER) // Carrega a indústria junto
    @JoinColumn(name = "industria_id") //Null ou medicamento puder ser cadastrado sem indústria inicialmente
    private Industria industria;
}
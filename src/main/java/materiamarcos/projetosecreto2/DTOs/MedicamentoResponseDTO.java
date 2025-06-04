package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String codigoDeBarras;
    private LocalDate validade;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private boolean promocao;
    private BigDecimal precoPromocional;
    private PrincipioAtivoResponseDTO principioAtivo;
    private IndustriaDTO industria;
}
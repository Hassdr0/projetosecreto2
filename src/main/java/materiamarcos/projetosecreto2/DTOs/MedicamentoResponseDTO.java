package materiamarcos.projetosecreto2.DTOs;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
    private PrincipioAtivoDTO principioAtivo; // DTO aninhado
    private IndustriaDTO industria;         // DTO aninhado
}
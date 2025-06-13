package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de um medicamento em respostas da API.")
public class MedicamentoResponseDTO {

    @Schema(description = "ID único do medicamento.")
    private Long id;

    @Schema(description = "Nome comercial do medicamento.")
    private String nome;

    @Schema(description = "Descrição detalhada do medicamento.")
    private String descricao;

    @Schema(description = "Código de barras do produto.")
    private String codigoDeBarras;

    @Schema(description = "Data de validade.")
    private LocalDate validade;

    @Schema(description = "Preço de custo do medicamento.")
    private BigDecimal precoCompra;

    @Schema(description = "Preço de venda ao consumidor.")
    private BigDecimal precoVenda;

    @Schema(description = "Indica se o medicamento está em promoção.")
    private boolean promocao;

    @Schema(description = "Preço especial de venda se estiver em promoção.")
    private BigDecimal precoPromocional;

    @Schema(description = "Detalhes do princípio ativo associado.")
    private PrincipioAtivoResponseDTO principioAtivo;

    @Schema(description = "Detalhes da indústria/fabricante associada.")
    private IndustriaDTO industria;
}

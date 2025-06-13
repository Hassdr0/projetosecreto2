package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO para criar ou atualizar um medicamento.")
public class MedicamentoRequestDTO {

    @NotBlank(message = "Nome do medicamento é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    @Schema(description = "Nome comercial do medicamento.", example = "Tylenol 750mg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Schema(description = "Descrição detalhada do medicamento.", example = "Analgésico e antitérmico para alívio de dores e febre.")
    private String descricao;

    @Size(max = 50, message = "Código de barras pode ter no máximo 50 caracteres")
    @Schema(description = "Código de barras do produto (GTIN/EAN).", example = "7891000123456")
    private String codigoDeBarras;

    @Schema(description = "Data de validade do lote.", example = "2026-12-31")
    private LocalDate validade;

    @NotNull(message = "Preço de compra é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de compra deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de compra inválido")
    @Schema(description = "Preço de custo do medicamento.", example = "5.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precoCompra;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Preço de venda deve ser positivo")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de venda inválido")
    @Schema(description = "Preço de venda ao consumidor.", example = "12.80", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precoVenda;

    @NotNull(message = "ID do Princípio Ativo é obrigatório")
    @Schema(description = "ID do Princípio Ativo associado.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long principioAtivoId;

    @Schema(description = "ID da Indústria/Fabricante associada.", example = "2")
    private Long industriaId;

    @Schema(description = "Indica se o produto está em promoção.", example = "false", defaultValue = "false")
    private Boolean promocao;

    @DecimalMin(value = "0.0", inclusive = true, message = "Preço promocional deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço promocional inválido")
    @Schema(description = "Preço especial de venda se o produto estiver em promoção.", example = "9.99")
    private BigDecimal precoPromocional;
}

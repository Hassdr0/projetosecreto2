package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicamentoRequestDTO {

    @NotBlank(message = "Nome do medicamento é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    private String nome;

    private String descricao;

    @Size(max = 50, message = "Código de barras pode ter no máximo 50 caracteres")
    private String codigoDeBarras;

    @FutureOrPresent(message = "Data de validade deve ser no presente ou futuro")
    private LocalDate validade;

    @NotNull(message = "Preço de compra é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de compra deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de compra inválido")
    private BigDecimal precoCompra;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Preço de venda deve ser positivo")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de venda inválido")
    private BigDecimal precoVenda;

    // ID para o Princípio Ativo, frontend enviará o ID de um princípio ativo existente.
    @NotNull(message = "ID do Princípio Ativo é obrigatório")
    private Long principioAtivoId;

    // ID para a Indústria, frontend enviará o ID de uma indústria existente.
    private Long industriaId; // Pode ser opcional se um medicamento puder ser cadastrado sem indústria

    // campos para promoção
    private Boolean promocao = false; // Default para false

    @DecimalMin(value = "0.0", inclusive = true, message = "Preço promocional deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço promocional inválido")
    private BigDecimal precoPromocional;
}
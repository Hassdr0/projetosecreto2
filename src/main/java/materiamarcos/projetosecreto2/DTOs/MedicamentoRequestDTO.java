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

    // A anotação @FutureOrPresent foi comentada para permitir datas passadas durante o teste.
    // Em um sistema de produção, você provavelmente iria querer reativá-la ou ter uma lógica de negócio específica.
    // @FutureOrPresent(message = "Data de validade deve ser no presente ou futuro")
    private LocalDate validade;

    @NotNull(message = "Preço de compra é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço de compra deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de compra inválido")
    private BigDecimal precoCompra;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Preço de venda deve ser positivo")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço de venda inválido")
    private BigDecimal precoVenda;

    @NotNull(message = "ID do Princípio Ativo é obrigatório")
    private Long principioAtivoId;

    private Long industriaId;

    private Boolean promocao = false;

    @DecimalMin(value = "0.0", inclusive = true, message = "Preço promocional deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço promocional inválido")
    private BigDecimal precoPromocional;
}
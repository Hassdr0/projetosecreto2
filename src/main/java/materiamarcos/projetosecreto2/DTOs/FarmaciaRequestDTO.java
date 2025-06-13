package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para criar ou atualizar uma farmácia/filial.")
public class FarmaciaRequestDTO {

    @NotBlank(message = "Nome da filial é obrigatório")
    @Size(min = 3, max = 100)
    @Schema(description = "Nome da farmácia/filial.", example = "Barateira Filial Centro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Size(max = 255)
    @Schema(description = "Endereço completo da filial.", example = "Rua Principal, 123, Sala 4")
    private String endereco;

    @Size(max = 100)
    @Schema(description = "Cidade da filial.", example = "Cidade Central")
    private String cidade;

    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    @Schema(description = "UF (Estado) da filial, com 2 caracteres.", example = "SP")
    private String uf;

    @Pattern(regexp = "^$|^([0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}|[0-9]{14})$", message = "Formato de CNPJ inválido")
    @Schema(description = "CNPJ da filial (opcional, formato com máscara ou apenas números).", example = "11.222.333/0001-44")
    private String cnpj;

    @Size(max = 20)
    @Schema(description = "Telefone de contacto da filial.", example = "(11) 5555-4444")
    private String telefone;
}

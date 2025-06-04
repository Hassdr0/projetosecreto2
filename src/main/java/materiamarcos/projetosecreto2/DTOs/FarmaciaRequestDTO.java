package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FarmaciaRequestDTO {
    @NotBlank(message = "Nome da filial é obrigatório")
    @Size(min = 3, max = 100)
    private String nome;

    @Size(max = 255)
    private String endereco;

    @Size(max = 100)
    private String cidade;

    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String uf;

    @Pattern(regexp = "^$|^([0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}|[0-9]{14})$", message = "Formato de CNPJ inválido")
    private String cnpj;

    @Size(max = 20)
    private String telefone;
}
package materiamarcos.projetosecreto2.DTOs; // Ajuste o pacote se necessário

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IndustriaRequestDTO {

    @NotBlank(message = "Nome da indústria é obrigatório")
    @Size(min = 2, max = 150, message = "Nome da indústria deve ter entre 2 e 150 caracteres")
    private String nome;

    // Validação para formato de CNPJ (XX.XXX.XXX/XXXX-XX)
    @Pattern(regexp = "^([0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}|[0-9]{14})$", message = "Formato de CNPJ inválido")
    @Size(max = 18, message = "CNPJ pode ter no máximo 18 caracteres com máscara")
    private String cnpj;
}
package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrincipioAtivoRequestDTO {

    @NotBlank(message = "Nome do princípio ativo é obrigatório")
    @Size(min = 2, max = 200, message = "Nome do princípio ativo deve ter entre 2 e 200 caracteres")
    private String nome;
}
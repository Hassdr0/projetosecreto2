package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para criar ou atualizar um princípio ativo.")
public class PrincipioAtivoRequestDTO {

    @NotBlank(message = "Nome do princípio ativo é obrigatório")
    @Size(min = 2, max = 200, message = "Nome do princípio ativo deve ter entre 2 e 200 caracteres")
    @Schema(description = "Nome do princípio ativo.", example = "Paracetamol", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;
}

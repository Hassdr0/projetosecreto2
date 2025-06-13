package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para criar ou atualizar uma indústria/fornecedor.")
public class IndustriaRequestDTO {

    @NotBlank(message = "Nome da indústria é obrigatório")
    @Size(min = 2, max = 150)
    @Schema(description = "Nome da indústria farmacêutica.", example = "Neo Química", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Pattern(regexp = "^$|^([0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}|[0-9]{14})$", message = "Formato de CNPJ inválido")
    @Size(max = 18)
    @Schema(description = "CNPJ da indústria (opcional, formato com máscara ou apenas números).", example = "22.333.444/0001-55")
    private String cnpj;
}

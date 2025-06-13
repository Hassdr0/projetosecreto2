package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para a requisição de login do usuário.")
public class LoginRequestDTO {

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Schema(description = "Nome de usuário para login.", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário.", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

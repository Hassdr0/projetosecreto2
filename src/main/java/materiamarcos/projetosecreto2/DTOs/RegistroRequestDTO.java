package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para registrar um novo usuário no sistema.")
public class RegistroRequestDTO {

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50)
    @Schema(description = "Nome de usuário único para o novo usuário.", example = "novo.cliente", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    @Schema(description = "Email único do novo usuário.", example = "cliente@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100)
    @Schema(description = "Senha para o novo usuário. Deve ter no mínimo 6 caracteres.", example = "senha1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

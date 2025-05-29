package materiamarcos.projetosecreto2.DTOs;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Lombok
public class LoginRequestDTO {

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
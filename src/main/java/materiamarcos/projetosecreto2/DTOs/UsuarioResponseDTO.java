package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para representar dados básicos de um usuário em respostas da API (sem dados sensíveis).")
public class UsuarioResponseDTO {

    @Schema(description = "ID único do usuário.", example = "1")
    private Long id;

    @Schema(description = "Nome de usuário.", example = "admin")
    private String username;

    @Schema(description = "Email do usuário.", example = "admin@barateira.com")
    private String email;

    public UsuarioResponseDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}

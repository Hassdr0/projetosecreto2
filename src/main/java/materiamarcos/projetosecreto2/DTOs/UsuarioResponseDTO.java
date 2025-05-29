package materiamarcos.projetosecreto2.DTOs;

import lombok.Data;

@Data // Lombok
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String email;
    // NUNCA retornar a senha, mesmo que criptografada, em respostas de API!

    public UsuarioResponseDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para a resposta de login, contendo o token JWT.")
public class JwtResponseDTO {

    @Schema(description = "O token de acesso JWT gerado.", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3ODg4NjQwMCwiZXhwIjoxNjc4OTcyODAwfQ.xxxxxxxx")
    private String token;

    @Schema(description = "Tipo do token, sempre 'Bearer'.", example = "Bearer", defaultValue = "Bearer")
    private String tipo = "Bearer";

    public JwtResponseDTO(String accessToken) {
        this.token = accessToken;
    }
}

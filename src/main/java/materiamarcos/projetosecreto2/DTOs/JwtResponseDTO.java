package materiamarcos.projetosecreto2.DTOs;

import lombok.Data;



@Data
public class JwtResponseDTO {
    private String token;
    private String tipo = "Bearer"; // Tipo padrão para tokens JWT

    // Construtor é necessário se não usar @AllArgsConstructor do Lombok ou se quiser lógica específica
    public JwtResponseDTO(String accessToken) {
        this.token = accessToken;
    }

}
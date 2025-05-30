package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
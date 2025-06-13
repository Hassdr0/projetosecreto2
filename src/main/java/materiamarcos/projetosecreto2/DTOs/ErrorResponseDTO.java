package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar uma resposta de erro padronizada da API.")
public class ErrorResponseDTO {

    @Schema(description = "Timestamp de quando o erro ocorreu (em milissegundos).", example = "1678886400000")
    private long timestamp;

    @Schema(description = "Código de status HTTP do erro.", example = "404")
    private int status;

    @Schema(description = "Breve descrição do status HTTP.", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem detalhada descrevendo o erro.", example = "Medicamento com ID 99 não encontrado.")
    private String message;

    @Schema(description = "Caminho (path) do endpoint que foi chamado.", example = "/api/medicamentos/99")
    private String path;
}

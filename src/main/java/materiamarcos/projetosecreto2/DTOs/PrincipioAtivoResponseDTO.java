package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de um princípio ativo em respostas da API.")
public class PrincipioAtivoResponseDTO {

    @Schema(description = "ID único do princípio ativo.", example = "1")
    private Long id;

    @Schema(description = "Nome do princípio ativo.", example = "Paracetamol")
    private String nome;
}

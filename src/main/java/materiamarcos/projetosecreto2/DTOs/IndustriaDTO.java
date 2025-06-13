package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de uma indústria/fornecedor em respostas da API.")
public class IndustriaDTO {

    @Schema(description = "ID único da indústria.", example = "1")
    private Long id;

    @Schema(description = "Nome da indústria.", example = "Neo Química")
    private String nome;

    @Schema(description = "CNPJ da indústria.", example = "22.333.444/0001-55")
    private String cnpj;
}

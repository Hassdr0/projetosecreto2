package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de uma farmácia/filial em respostas da API.")
public class FarmaciaResponseDTO {

    @Schema(description = "ID único da farmácia.")
    private Long id;

    @Schema(description = "Nome da farmácia.")
    private String nome;

    @Schema(description = "Endereço completo.")
    private String endereco;

    @Schema(description = "Cidade.")
    private String cidade;

    @Schema(description = "UF (Estado).")
    private String uf;

    @Schema(description = "CNPJ.")
    private String cnpj;

    @Schema(description = "Telefone.")
    private String telefone;
}

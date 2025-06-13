package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar os dados de um cliente em respostas da API.")
public class ClienteResponseDTO {
    @Schema(description = "ID único do cliente.")
    private Long id;
    @Schema(description = "Nome completo do cliente.")
    private String nome;
    @Schema(description = "CPF do cliente.")
    private String cpf;
    @Schema(description = "Email do cliente.")
    private String email;
    @Schema(description = "Telefone do cliente.")
    private String telefone;
    @Schema(description = "Logradouro do cliente.")
    private String logradouro;
    @Schema(description = "Número do endereço.")
    private String numero;
    @Schema(description = "Complemento do endereço.")
    private String complemento;
    @Schema(description = "Bairro do endereço.")
    private String bairro;
    @Schema(description = "Cidade do endereço.")
    private String cidade;
    @Schema(description = "UF do endereço.")
    private String uf;
    @Schema(description = "CEP do endereço.")
    private String cep;
}

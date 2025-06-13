package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para criar ou atualizar um cliente.")
public class ClienteRequestDTO {

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 3, max = 150)
    @Schema(description = "Nome completo do cliente.", example = "Carlos Cliente Fiel", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Schema(description = "CPF do cliente (formato com máscara).", example = "444.555.666-77")
    private String cpf;

    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    @Schema(description = "Email de contato do cliente.", example = "carlos.fiel@email.com")
    private String email;

    @Size(max = 20)
    @Schema(description = "Telefone de contato do cliente.", example = "(11) 98765-4321")
    private String telefone;

    @Size(max = 200)
    @Schema(description = "Logradouro (Rua, Avenida, etc.) do endereço.", example = "Rua das Flores")
    private String logradouro;

    @Size(max = 20)
    @Schema(description = "Número do endereço.", example = "123")
    private String numero;

    @Size(max = 100)
    @Schema(description = "Complemento do endereço (opcional).", example = "Apto 42B")
    private String complemento;

    @Size(max = 100)
    @Schema(description = "Bairro do endereço.", example = "Jardim Paraíso")
    private String bairro;

    @Size(max = 100)
    @Schema(description = "Cidade do endereço.", example = "Cidade Bela")
    private String cidade;

    @Size(min = 2, max = 2)
    @Schema(description = "UF (Estado) do endereço, com 2 caracteres.", example = "SP")
    private String uf;

    @Pattern(regexp = "^$|^\\d{5}\\-\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX ou ser vazio")
    @Schema(description = "CEP do endereço (formato com máscara).", example = "12345-001")
    private String cep;
}

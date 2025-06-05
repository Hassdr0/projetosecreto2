package materiamarcos.projetosecreto2.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 3, max = 150)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Size(max = 14)
    private String cpf;

    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefone;

    @Size(max = 200)
    private String logradouro;

    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    @Size(max = 100)
    private String cidade;

    @Size(min = 2, max = 2)
    private String uf;

    @Pattern(regexp = "^$|^\\d{5}\\-\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX ou ser vazio")
    @Size(max = 9)
    private String cep;
}
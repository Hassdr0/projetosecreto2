package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmaciaResponseDTO {
    private Long id;
    private String nome;
    private String endereco;
    private String cidade;
    private String uf;
    private String cnpj;
    private String telefone;
}
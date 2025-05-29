package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndustriaDTO {
    private Long id;
    private String nome;
    private String cnpj;
}
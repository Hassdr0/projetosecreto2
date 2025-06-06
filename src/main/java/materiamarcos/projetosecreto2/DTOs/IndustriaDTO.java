package materiamarcos.projetosecreto2.DTOs; // Ajuste o pacote se necessário

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndustriaDTO { // Pode ser IndustriaResponseDTO
    private Long id;
    private String nome;
    private String cnpj;
}
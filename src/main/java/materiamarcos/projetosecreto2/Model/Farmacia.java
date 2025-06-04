package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmacias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da filial é obrigatório")
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String nome; // Ex: "Filial Centro", "Barateira - Shopping Norte"

    @Size(max = 255)
    private String endereco;

    @Size(max = 100)
    private String cidade;

    @Size(min = 2, max = 2)
    private String uf; // Estado, ex: "SP", "RJ"

    @Column(length = 18, unique = true) // Formato: XX.XXX.XXX/XXXX-XX
    private String cnpj;

    @Size(max = 20)
    private String telefone;
}
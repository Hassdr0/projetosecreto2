package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "industrias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Industria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(length = 18, unique = true) // CNPJ (ex: XX.XXX.XXX/XXXX-XX)
    private String cnpj;

    public Industria(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
    }
}
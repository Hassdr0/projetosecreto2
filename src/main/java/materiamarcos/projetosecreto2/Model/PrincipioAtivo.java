package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "principios_ativos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrincipioAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String nome;


    public PrincipioAtivo(String nome) {
        this.nome = nome;
    }
}
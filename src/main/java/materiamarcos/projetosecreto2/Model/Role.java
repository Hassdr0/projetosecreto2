package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import materiamarcos.projetosecreto2.Model.enums.ERole;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private ERole nome;

    public Role(ERole nome) {
        this.nome = nome;
    }
}
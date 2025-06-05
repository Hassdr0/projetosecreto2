package materiamarcos.projetosecreto2.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String nome;

    @Pattern(regexp = "^$|^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou ser vazio")
    @Column(unique = true, length = 14)
    private String cpf;

    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    @Column(unique = true, length = 100)
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
    private String uf; // Estado

    @Pattern(regexp = "^$|^\\d{5}\\-\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX ou ser vazio")
    @Size(max = 9)
    private String cep;

    // Relacionamento opcional com User, se um cliente também puder ser um usuário do sistema
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    // private User user;
}

package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.Balconista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BalconistaRepository extends JpaRepository<Balconista, Long> {
    Optional<Balconista> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Optional<Balconista> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
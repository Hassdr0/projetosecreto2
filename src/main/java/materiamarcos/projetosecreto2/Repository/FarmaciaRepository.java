package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.Farmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmaciaRepository extends JpaRepository<Farmacia, Long> {
    Optional<Farmacia> findByNomeIgnoreCase(String nome);
    Optional<Farmacia> findByCnpj(String cnpj);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByCnpj(String cnpj);
}
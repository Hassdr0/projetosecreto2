package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.Industria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndustriaRepository extends JpaRepository<Industria, Long> {
    Optional<Industria> findByNomeIgnoreCase(String nome);
    Optional<Industria> findByCnpj(String cnpj);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByCnpj(String cnpj);
}
package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipioAtivoRepository extends JpaRepository<PrincipioAtivo, Long> {
    // Util para evitar duplicatas ou encontrar pelo nome

    Optional<PrincipioAtivo> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);

    Optional<Cliente> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
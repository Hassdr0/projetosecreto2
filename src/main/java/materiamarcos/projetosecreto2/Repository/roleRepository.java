package materiamarcos.projetosecreto2.Repository; // Verifique se o pacote está correto

import materiamarcos.projetosecreto2.Model.Role;
import materiamarcos.projetosecreto2.Model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface roleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByNome(ERole nome);

}
package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Controller.Teste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TesteRepository extends JpaRepository<Teste, Long> {

}
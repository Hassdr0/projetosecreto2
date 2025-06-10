package materiamarcos.projetosecreto2.Repository;
import materiamarcos.projetosecreto2.Model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    // Métodos de busca adicionais podem ser adicionados aqui
}
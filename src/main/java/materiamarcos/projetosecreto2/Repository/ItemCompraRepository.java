package materiamarcos.projetosecreto2.Repository;
import materiamarcos.projetosecreto2.Model.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {
    // Métodos de busca adicionais podem ser adicionados aqui
}
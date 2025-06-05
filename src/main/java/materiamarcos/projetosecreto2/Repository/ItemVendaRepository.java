package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda(Venda venda);
    List<ItemVenda> findByMedicamento(Medicamento medicamento);
}
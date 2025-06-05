package materiamarcos.projetosecreto2.Repository;

import materiamarcos.projetosecreto2.Model.Balconista;
import materiamarcos.projetosecreto2.Model.Cliente;
import materiamarcos.projetosecreto2.Model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    //úteis no futuro:
    List<Venda> findByBalconista(Balconista balconista);
    List<Venda> findByCliente(Cliente cliente);
    List<Venda> findByDataVendaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Venda> findByFarmaciaId(Long farmaciaId);

}
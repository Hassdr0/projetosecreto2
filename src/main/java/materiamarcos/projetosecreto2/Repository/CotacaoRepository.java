package materiamarcos.projetosecreto2.Repository;
import materiamarcos.projetosecreto2.Model.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
    List<Cotacao> findByMedicamentoIdAndAtivaTrueOrderByPrecoUnitarioCotadoAsc(Long medicamentoId);
}
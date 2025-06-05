package materiamarcos.projetosecreto2.Repository;
import materiamarcos.projetosecreto2.Model.Estoque;
import materiamarcos.projetosecreto2.Model.Farmacia;
import materiamarcos.projetosecreto2.Model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByMedicamentoAndFarmaciaAndLote(Medicamento medicamento, Farmacia farmacia, String lote);
    List<Estoque> findByMedicamentoAndFarmacia(Medicamento medicamento, Farmacia farmacia); // Se não usar lote ou para estoque geral
    List<Estoque> findByMedicamento(Medicamento medicamento);
    List<Estoque> findByFarmacia(Farmacia farmacia);
    List<Estoque> findByMedicamentoAndQuantidadeLessThanEqual(Medicamento medicamento, Integer quantidade);
    boolean existsByFarmaciaId(Long farmaciaId);

    @Query("SELECT SUM(e.quantidade) FROM Estoque e WHERE e.medicamento.principioAtivo.id = :principioAtivoId")
    Integer sumQuantidadeByPrincipioAtivoId(@Param("principioAtivoId") Long principioAtivoId);

    @Query("SELECT SUM(e.quantidade) FROM Estoque e WHERE e.medicamento.principioAtivo.id = :principioAtivoId AND e.farmacia.id = :farmaciaId")
    Integer sumQuantidadeByPrincipioAtivoIdAndFarmaciaId(@Param("principioAtivoId") Long principioAtivoId, @Param("farmaciaId") Long farmaciaId);
}
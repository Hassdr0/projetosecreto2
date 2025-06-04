package materiamarcos.projetosecreto2.Repository;


import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import materiamarcos.projetosecreto2.Model.Industria;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    Optional<Medicamento> findByCodigoDeBarras(String codigoDeBarras);
    List<Medicamento> findByNomeContainingIgnoreCase(String nome);
    List<Medicamento> findByPrincipioAtivo(PrincipioAtivo principioAtivo);
    List<Medicamento> findByIndustria(Industria industria);
    boolean existsByCodigoDeBarras(String codigoDeBarras);
}
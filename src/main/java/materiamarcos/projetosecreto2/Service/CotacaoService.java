package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.CotacaoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.CotacaoResponseDTO; // Precisaremos deste DTO
import materiamarcos.projetosecreto2.Model.Cotacao;
import materiamarcos.projetosecreto2.Model.Industria;
import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Repository.CotacaoRepository;
import materiamarcos.projetosecreto2.Repository.IndustriaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotacaoService {

    @Autowired
    private CotacaoRepository cotacaoRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private IndustriaRepository industriaRepository;

    private CotacaoResponseDTO toResponseDTO(Cotacao cotacao) {
        return new CotacaoResponseDTO(
                cotacao.getId(),
                cotacao.getMedicamento().getId(),
                cotacao.getMedicamento().getNome(),
                cotacao.getIndustria().getId(),
                cotacao.getIndustria().getNome(),
                cotacao.getPrecoUnitarioCotado(),
                cotacao.getDataCotacao(),
                cotacao.isAtiva()
        );
    }

    @Transactional
    public CotacaoResponseDTO registrarCotacao(CotacaoRequestDTO requestDTO) {
        Medicamento medicamento = medicamentoRepository.findById(requestDTO.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + requestDTO.getMedicamentoId() + " não encontrado."));

        Industria industria = industriaRepository.findById(requestDTO.getIndustriaId())
                .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + requestDTO.getIndustriaId() + " não encontrada."));

        Cotacao novaCotacao = new Cotacao();
        novaCotacao.setMedicamento(medicamento);
        novaCotacao.setIndustria(industria);
        novaCotacao.setPrecoUnitarioCotado(requestDTO.getPrecoUnitarioCotado());
        novaCotacao.setDataCotacao(LocalDate.now());
        novaCotacao.setAtiva(true);

        Cotacao cotacaoSalva = cotacaoRepository.save(novaCotacao);
        return toResponseDTO(cotacaoSalva);
    }

    @Transactional(readOnly = true)
    public List<CotacaoResponseDTO> buscarCotacoesAtivasPorMedicamento(Long medicamentoId) {
        return cotacaoRepository.findByMedicamentoIdAndAtivaTrueOrderByPrecoUnitarioCotadoAsc(medicamentoId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
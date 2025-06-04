package materiamarcos.projetosecreto2.Service; // Ajuste o pacote

import materiamarcos.projetosecreto2.DTOs.IndustriaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaDTO; // Ou IndustriaResponseDTO
import materiamarcos.projetosecreto2.Model.Industria;
import materiamarcos.projetosecreto2.Model.Medicamento; // Para verificar se a indústria está em uso
import materiamarcos.projetosecreto2.Repository.IndustriaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository; // Para verificar se a indústria está em uso
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Para o delete
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para StringUtils.hasText

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndustriaService {

    @Autowired
    private IndustriaRepository industriaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository; // Para verificar se a indústria está em uso

    // Helper para converter Entidade para DTO
    private IndustriaDTO converterParaDTO(Industria industria) {
        return new IndustriaDTO(industria.getId(), industria.getNome(), industria.getCnpj());
    }

    // Helper para normalizar CNPJ (remover máscara)
    private String normalizarCnpj(String cnpj) {
        if (cnpj == null) return null;
        return cnpj.replaceAll("[^0-9]", "");
    }

    @Transactional
    public IndustriaDTO criarIndustria(IndustriaRequestDTO requestDTO) {
        if (industriaRepository.existsByNomeIgnoreCase(requestDTO.getNome())) {
            throw new IllegalArgumentException("Indústria com o nome '" + requestDTO.getNome() + "' já existe.");
        }
        String cnpjNormalizado = null;
        if (StringUtils.hasText(requestDTO.getCnpj())) {
            cnpjNormalizado = normalizarCnpj(requestDTO.getCnpj());
            if (industriaRepository.existsByCnpj(cnpjNormalizado)) {
                throw new IllegalArgumentException("Indústria com o CNPJ '" + requestDTO.getCnpj() + "' já existe.");
            }
        }

        Industria novaIndustria = new Industria();
        novaIndustria.setNome(requestDTO.getNome());
        novaIndustria.setCnpj(cnpjNormalizado); // Salva CNPJ normalizado

        Industria salva = industriaRepository.save(novaIndustria);
        return converterParaDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<IndustriaDTO> listarTodas() {
        return industriaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IndustriaDTO buscarPorId(Long id) {
        Industria industria = industriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + id + " não encontrada."));
        return converterParaDTO(industria);
    }

    @Transactional
    public IndustriaDTO atualizarIndustria(Long id, IndustriaRequestDTO requestDTO) {
        Industria existente = industriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + id + " não encontrada para atualização."));

        // Verifica se o novo nome já existe em outro registro
        if (!existente.getNome().equalsIgnoreCase(requestDTO.getNome()) &&
                industriaRepository.existsByNomeIgnoreCase(requestDTO.getNome())) {
            throw new IllegalArgumentException("Outra Indústria com o nome '" + requestDTO.getNome() + "' já existe.");
        }

        String cnpjNormalizadoAntigo = existente.getCnpj();
        String novoCnpjNormalizado = null;
        if (StringUtils.hasText(requestDTO.getCnpj())) {
            novoCnpjNormalizado = normalizarCnpj(requestDTO.getCnpj());
            if ( (cnpjNormalizadoAntigo == null || !cnpjNormalizadoAntigo.equals(novoCnpjNormalizado) ) &&
                    industriaRepository.existsByCnpj(novoCnpjNormalizado) ) {
                throw new IllegalArgumentException("Outra Indústria com o CNPJ '" + requestDTO.getCnpj() + "' já existe.");
            }
        }

        existente.setNome(requestDTO.getNome());
        existente.setCnpj(novoCnpjNormalizado); // Atualiza com CNPJ normalizado

        Industria atualizada = industriaRepository.save(existente);
        return converterParaDTO(atualizada);
    }

    @Transactional
    public void deletarIndustria(Long id) {
        Industria industria = industriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + id + " não encontrada para deleção."));

        // Verifica se a indústria está sendo usada por algum medicamento
        List<Medicamento> medicamentosAssociados = medicamentoRepository.findByIndustria(industria);
        if (!medicamentosAssociados.isEmpty()) {
            throw new DataIntegrityViolationException("Não é possível deletar a indústria '" + industria.getNome() + "' pois ela está associada a " + medicamentosAssociados.size() + " medicamento(s).");
        }

        industriaRepository.deleteById(id);
    }
}
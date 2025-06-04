package materiamarcos.projetosecreto2.Service; // Ajuste o pacote

import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO; // Ou PrincipioAtivoResponseDTO
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import materiamarcos.projetosecreto2.Repository.PrincipioAtivoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrincipioAtivoService {

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    // Helper para converter Entidade para DTO
    private PrincipioAtivoResponseDTO converterParaDTO(PrincipioAtivo principioAtivo) {
        return new PrincipioAtivoResponseDTO(principioAtivo.getId(), principioAtivo.getNome());
    }

    @Transactional
    public PrincipioAtivoResponseDTO criarPrincipioAtivo(PrincipioAtivoRequestDTO requestDTO) {
        if (principioAtivoRepository.existsByNomeIgnoreCase(requestDTO.getNome())) {
            throw new IllegalArgumentException("Princípio Ativo com o nome '" + requestDTO.getNome() + "' já existe.");
        }
        PrincipioAtivo novoPrincipioAtivo = new PrincipioAtivo();
        novoPrincipioAtivo.setNome(requestDTO.getNome());
        PrincipioAtivo salvo = principioAtivoRepository.save(novoPrincipioAtivo);
        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<PrincipioAtivoResponseDTO> listarTodos() {
        return principioAtivoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrincipioAtivoResponseDTO buscarPorId(Long id) {
        PrincipioAtivo principioAtivo = principioAtivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + id + " não encontrado."));
        return converterParaDTO(principioAtivo);
    }

    @Transactional
    public PrincipioAtivoResponseDTO atualizarPrincipioAtivo(Long id, PrincipioAtivoRequestDTO requestDTO) {
        PrincipioAtivo existente = principioAtivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + id + " não encontrado para atualização."));

        // Verifica se o novo nome já existe em outro registro
        if (!existente.getNome().equalsIgnoreCase(requestDTO.getNome()) &&
                principioAtivoRepository.existsByNomeIgnoreCase(requestDTO.getNome())) {
            throw new IllegalArgumentException("Outro Princípio Ativo com o nome '" + requestDTO.getNome() + "' já existe.");
        }

        existente.setNome(requestDTO.getNome());
        PrincipioAtivo atualizado = principioAtivoRepository.save(existente);
        return converterParaDTO(atualizado);
    }

    @Transactional
    public void deletarPrincipioAtivo(Long id) {
        if (!principioAtivoRepository.existsById(id)) {
            throw new EntityNotFoundException("Princípio Ativo com ID " + id + " não encontrado para deleção.");
        }
        // TODO: Adicionar verificação aqui: um Princípio Ativo só pode ser deletado
        // se não estiver sendo usado por nenhum Medicamento.
        // Isso exigiria injetar o MedicamentoRepository e fazer uma consulta.
        // Ex: if (medicamentoRepository.existsByPrincipioAtivoId(id)) { throw new DataIntegrityViolationException(...); }
        principioAtivoRepository.deleteById(id);
    }
}
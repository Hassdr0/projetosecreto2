package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.BalconistaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.BalconistaResponseDTO;
import materiamarcos.projetosecreto2.Model.Balconista;
import materiamarcos.projetosecreto2.Repository.BalconistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalconistaService {

    @Autowired
    private BalconistaRepository balconistaRepository;

    // @Autowired
    // private VendaRepository vendaRepository; // Para verificar se balconista tem vendas antes de deletar

    private BalconistaResponseDTO toResponseDTO(Balconista balconista) {
        return new BalconistaResponseDTO(
                balconista.getId(),
                balconista.getNome(),
                balconista.getCpf(),
                balconista.getTaxaComissao(),
                balconista.getSalarioBase()
        );
    }


    private String normalizeCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9]", "");
    }

    @Transactional
    public BalconistaResponseDTO criarBalconista(BalconistaRequestDTO requestDTO) {

        String cpfParaVerificacao = null;
        if (StringUtils.hasText(requestDTO.getCpf())) {
            cpfParaVerificacao = normalizeCpf(requestDTO.getCpf());
            if (balconistaRepository.existsByCpf(requestDTO.getCpf())) { // Verifica com o formato enviado (com máscara)
                throw new IllegalArgumentException("Balconista com o CPF '" + requestDTO.getCpf() + "' já existe.");
            }
        }

        Balconista novoBalconista = new Balconista();
        novoBalconista.setNome(requestDTO.getNome());
        novoBalconista.setCpf(requestDTO.getCpf());
        novoBalconista.setTaxaComissao(requestDTO.getTaxaComissao());
        novoBalconista.setSalarioBase(requestDTO.getSalarioBase());

        Balconista salvo = balconistaRepository.save(novoBalconista);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<BalconistaResponseDTO> listarTodos() {
        return balconistaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BalconistaResponseDTO buscarPorId(Long id) {
        Balconista balconista = balconistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balconista com ID " + id + " não encontrado."));
        return toResponseDTO(balconista);
    }

    @Transactional
    public BalconistaResponseDTO atualizarBalconista(Long id, BalconistaRequestDTO requestDTO) {
        Balconista existente = balconistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balconista com ID " + id + " não encontrado para atualização."));

        if (StringUtils.hasText(requestDTO.getCpf())) {
            String novoCpf = requestDTO.getCpf();

            if (!novoCpf.equals(existente.getCpf()) &&
                    balconistaRepository.findByCpf(novoCpf).filter(b -> !b.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Outro balconista com o CPF '" + requestDTO.getCpf() + "' já existe.");
            }
            existente.setCpf(novoCpf);
        } else {
            existente.setCpf(null);
        }

        existente.setNome(requestDTO.getNome());
        existente.setTaxaComissao(requestDTO.getTaxaComissao());
        existente.setSalarioBase(requestDTO.getSalarioBase());

        Balconista atualizado = balconistaRepository.save(existente);
        return toResponseDTO(atualizado);
    }

    @Transactional
    public void deletarBalconista(Long id) {
        Balconista balconista = balconistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balconista com ID " + id + " não encontrado para deleção."));

        // TODO: Verificar se o balconista está associado a vendas.
        // if (vendaRepository.existsByBalconistaId(id)) {
        //     throw new DataIntegrityViolationException("Não é possível deletar o balconista '" + balconista.getNome() + "' pois ele possui vendas associadas.");
        // }

        balconistaRepository.delete(balconista);
    }
}
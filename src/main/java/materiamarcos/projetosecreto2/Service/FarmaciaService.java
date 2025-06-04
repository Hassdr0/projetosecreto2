package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.FarmaciaRequestDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaResponseDTO;
import materiamarcos.projetosecreto2.Model.Farmacia;
import materiamarcos.projetosecreto2.Repository.FarmaciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmaciaService {

    @Autowired
    private FarmaciaRepository farmaciaRepository;

    private FarmaciaResponseDTO toDTO(Farmacia farmacia) {
        return new FarmaciaResponseDTO(farmacia.getId(), farmacia.getNome(), farmacia.getEndereco(),
                farmacia.getCidade(), farmacia.getUf(), farmacia.getCnpj(), farmacia.getTelefone());
    }

    private String normalizeCnpj(String cnpj) {
        if (cnpj == null) return null;
        return cnpj.replaceAll("[^0-9]", "");
    }

    @Transactional
    public FarmaciaResponseDTO criar(FarmaciaRequestDTO dto) {
        if (farmaciaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma farmácia com o nome: " + dto.getNome());
        }
        String cnpjNormalizado = null;
        if (StringUtils.hasText(dto.getCnpj())) {
            cnpjNormalizado = normalizeCnpj(dto.getCnpj());
            if (farmaciaRepository.existsByCnpj(cnpjNormalizado)) {
                throw new IllegalArgumentException("Já existe uma farmácia com o CNPJ: " + dto.getCnpj());
            }
        }

        Farmacia farmacia = new Farmacia();
        farmacia.setNome(dto.getNome());
        farmacia.setEndereco(dto.getEndereco());
        farmacia.setCidade(dto.getCidade());
        farmacia.setUf(dto.getUf());
        farmacia.setCnpj(cnpjNormalizado);
        farmacia.setTelefone(dto.getTelefone());
        return toDTO(farmaciaRepository.save(farmacia));
    }

    @Transactional(readOnly = true)
    public List<FarmaciaResponseDTO> listarTodas() {
        return farmaciaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FarmaciaResponseDTO buscarPorId(Long id) {
        return farmaciaRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + id + " não encontrada."));
    }

    @Transactional
    public FarmaciaResponseDTO atualizar(Long id, FarmaciaRequestDTO dto) {
        Farmacia farmacia = farmaciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + id + " não encontrada."));

        if (!farmacia.getNome().equalsIgnoreCase(dto.getNome()) && farmaciaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new IllegalArgumentException("Já existe outra farmácia com o nome: " + dto.getNome());
        }

        String novoCnpjNormalizado = null;
        if (StringUtils.hasText(dto.getCnpj())) {
            novoCnpjNormalizado = normalizeCnpj(dto.getCnpj());
            if ((farmacia.getCnpj() == null || !farmacia.getCnpj().equals(novoCnpjNormalizado)) && farmaciaRepository.existsByCnpj(novoCnpjNormalizado) ) {
                throw new IllegalArgumentException("Já existe outra farmácia com o CNPJ: " + dto.getCnpj());
            }
        }

        farmacia.setNome(dto.getNome());
        farmacia.setEndereco(dto.getEndereco());
        farmacia.setCidade(dto.getCidade());
        farmacia.setUf(dto.getUf());
        farmacia.setCnpj(novoCnpjNormalizado);
        farmacia.setTelefone(dto.getTelefone());
        return toDTO(farmaciaRepository.save(farmacia));
    }

    @Transactional
    public void deletar(Long id) {
        if (!farmaciaRepository.existsById(id)) {
            throw new EntityNotFoundException("Farmácia com ID " + id + " não encontrada.");
        }
        // TODO: Adicionar verificação se a farmácia tem estoque/vendas antes de deletar
        farmaciaRepository.deleteById(id);
    }
}
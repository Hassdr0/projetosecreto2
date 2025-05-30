package materiamarcos.projetosecreto2.Service; // Ajuste o pacote

import materiamarcos.projetosecreto2.DTOs.IndustriaDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoDTO;
import materiamarcos.projetosecreto2.Model.Industria;
import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import materiamarcos.projetosecreto2.Repository.IndustriaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository;
import materiamarcos.projetosecreto2.Repository.PrincipioAtivoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    @Autowired
    private IndustriaRepository industriaRepository;

    // Método helper privado para converter Entidade Medicamento para MedicamentoResponseDTO
    private MedicamentoResponseDTO converterParaResponseDTO(Medicamento medicamento) {
        MedicamentoResponseDTO responseDTO = new MedicamentoResponseDTO();
        responseDTO.setId(medicamento.getId());
        responseDTO.setNome(medicamento.getNome());
        responseDTO.setDescricao(medicamento.getDescricao());
        responseDTO.setCodigoDeBarras(medicamento.getCodigoDeBarras());
        responseDTO.setValidade(medicamento.getValidade());
        responseDTO.setPrecoCompra(medicamento.getPrecoCompra());
        responseDTO.setPrecoVenda(medicamento.getPrecoVenda());
        responseDTO.setPromocao(medicamento.isPromocao());
        responseDTO.setPrecoPromocional(medicamento.getPrecoPromocional());

        if (medicamento.getPrincipioAtivo() != null) {
            PrincipioAtivoDTO paDTO = new PrincipioAtivoDTO();
            paDTO.setId(medicamento.getPrincipioAtivo().getId());
            paDTO.setNome(medicamento.getPrincipioAtivo().getNome());
            responseDTO.setPrincipioAtivo(paDTO);
        }

        if (medicamento.getIndustria() != null) {
            IndustriaDTO indDTO = new IndustriaDTO();
            indDTO.setId(medicamento.getIndustria().getId());
            indDTO.setNome(medicamento.getIndustria().getNome());
            indDTO.setCnpj(medicamento.getIndustria().getCnpj());
            responseDTO.setIndustria(indDTO);
        }
        return responseDTO;
    }

    @Transactional
    public MedicamentoResponseDTO criarMedicamento(MedicamentoRequestDTO requestDTO) {
        if (requestDTO.getCodigoDeBarras() != null && !requestDTO.getCodigoDeBarras().isBlank() &&
                medicamentoRepository.existsByCodigoDeBarras(requestDTO.getCodigoDeBarras())) {
            throw new IllegalArgumentException("Medicamento com o código de barras '" + requestDTO.getCodigoDeBarras() + "' já existe.");
        }

        PrincipioAtivo principioAtivo = principioAtivoRepository.findById(requestDTO.getPrincipioAtivoId())
                .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + requestDTO.getPrincipioAtivoId() + " não encontrado."));

        Industria industria = null;
        if (requestDTO.getIndustriaId() != null) {
            industria = industriaRepository.findById(requestDTO.getIndustriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + requestDTO.getIndustriaId() + " não encontrada."));
        }

        Medicamento novoMedicamento = new Medicamento();
        novoMedicamento.setNome(requestDTO.getNome());
        novoMedicamento.setDescricao(requestDTO.getDescricao());
        novoMedicamento.setCodigoDeBarras(requestDTO.getCodigoDeBarras());
        novoMedicamento.setValidade(requestDTO.getValidade());
        novoMedicamento.setPrecoCompra(requestDTO.getPrecoCompra());
        novoMedicamento.setPrecoVenda(requestDTO.getPrecoVenda());
        novoMedicamento.setPrincipioAtivo(principioAtivo);
        novoMedicamento.setIndustria(industria);

        if (requestDTO.getPromocao() != null) {
            novoMedicamento.setPromocao(requestDTO.getPromocao());
            if (requestDTO.getPromocao() && requestDTO.getPrecoPromocional() != null) {
                // TODO: Adicionar validação da regra de negócio da promoção aqui
                novoMedicamento.setPrecoPromocional(requestDTO.getPrecoPromocional());
            } else {
                novoMedicamento.setPrecoPromocional(null);
            }
        }

        Medicamento medicamentoSalvo = medicamentoRepository.save(novoMedicamento);
        return converterParaResponseDTO(medicamentoSalvo);
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarTodos() {
        List<Medicamento> medicamentos = medicamentoRepository.findAll();
        if (medicamentos.isEmpty()) {
            return Collections.emptyList();
        }
        return medicamentos.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO buscarPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + id + " não encontrado."));
        return converterParaResponseDTO(medicamento);
    }

    @Transactional
    public MedicamentoResponseDTO atualizarMedicamento(Long id, MedicamentoRequestDTO requestDTO) {
        Medicamento medicamentoExistente = medicamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + id + " não encontrado para atualização."));

        if (requestDTO.getCodigoDeBarras() != null &&
                !requestDTO.getCodigoDeBarras().isBlank() &&
                !requestDTO.getCodigoDeBarras().equals(medicamentoExistente.getCodigoDeBarras())) {
            if (medicamentoRepository.existsByCodigoDeBarras(requestDTO.getCodigoDeBarras())) {
                throw new IllegalArgumentException("Outro medicamento já existe com o código de barras: " + requestDTO.getCodigoDeBarras());
            }
            medicamentoExistente.setCodigoDeBarras(requestDTO.getCodigoDeBarras());
        }

        if (requestDTO.getPrincipioAtivoId() != null) {
            PrincipioAtivo pa = principioAtivoRepository.findById(requestDTO.getPrincipioAtivoId())
                    .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + requestDTO.getPrincipioAtivoId() + " não encontrado."));
            medicamentoExistente.setPrincipioAtivo(pa);
        }

        if (requestDTO.getIndustriaId() != null) {
            Industria ind = industriaRepository.findById(requestDTO.getIndustriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + requestDTO.getIndustriaId() + " não encontrada."));
            medicamentoExistente.setIndustria(ind);
        } else {
            medicamentoExistente.setIndustria(null);
        }

        medicamentoExistente.setNome(requestDTO.getNome());
        medicamentoExistente.setDescricao(requestDTO.getDescricao());
        medicamentoExistente.setValidade(requestDTO.getValidade());
        medicamentoExistente.setPrecoCompra(requestDTO.getPrecoCompra());
        medicamentoExistente.setPrecoVenda(requestDTO.getPrecoVenda());

        if (requestDTO.getPromocao() != null) {
            medicamentoExistente.setPromocao(requestDTO.getPromocao());
            if (requestDTO.getPromocao() && requestDTO.getPrecoPromocional() != null) {
                // TODO: Adicionar validação da regra de negócio da promoção aqui
                medicamentoExistente.setPrecoPromocional(requestDTO.getPrecoPromocional());
            } else {
                medicamentoExistente.setPrecoPromocional(null);
            }
        }

        Medicamento medicamentoAtualizado = medicamentoRepository.save(medicamentoExistente);
        return converterParaResponseDTO(medicamentoAtualizado);
    }

    @Transactional
    public void deletarMedicamento(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Medicamento com ID " + id + " não encontrado para deleção.");
        }
        medicamentoRepository.deleteById(id);
    }
}
package materiamarcos.projetosecreto2.Service; // Verifique se o pacote está correto

import materiamarcos.projetosecreto2.DTOs.IndustriaDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
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

import java.math.BigDecimal;
import java.math.RoundingMode; // Import para arredondamento
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
            PrincipioAtivoResponseDTO paDTO = new PrincipioAtivoResponseDTO(
                    medicamento.getPrincipioAtivo().getId(),
                    medicamento.getPrincipioAtivo().getNome()
            );
            responseDTO.setPrincipioAtivo(paDTO);
        }

        if (medicamento.getIndustria() != null) {
            IndustriaDTO indDTO = new IndustriaDTO(
                    medicamento.getIndustria().getId(),
                    medicamento.getIndustria().getNome(),
                    medicamento.getIndustria().getCnpj()
            );
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

        // Aplica lógica de promoção também na criação, se informado
        if (Boolean.TRUE.equals(requestDTO.getPromocao())) {
            if (medicamentoRepository.existsByPrincipioAtivoAndPromocaoIsTrue(principioAtivo)) {
                throw new IllegalArgumentException("Já existe um medicamento com o mesmo princípio ativo ('" + principioAtivo.getNome() + "') em promoção.");
            }

            if (requestDTO.getPrecoPromocional() == null || requestDTO.getPrecoPromocional().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Preço promocional é obrigatório e deve ser positivo para ativar uma promoção.");
            }

            BigDecimal precoMinimoPromocao = novoMedicamento.getPrecoCompra().multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.HALF_UP);
            if (requestDTO.getPrecoPromocional().compareTo(precoMinimoPromocao) < 0) {
                throw new IllegalArgumentException("O preço promocional (R$ " + requestDTO.getPrecoPromocional() +
                        ") não pode ser inferior a 110% do preço de compra (R$ " + precoMinimoPromocao + ").");
            }

            novoMedicamento.setPromocao(true);
            novoMedicamento.setPrecoPromocional(requestDTO.getPrecoPromocional());
        } else {
            novoMedicamento.setPromocao(false);
            novoMedicamento.setPrecoPromocional(null);
        }

        Medicamento medicamentoSalvo = medicamentoRepository.save(novoMedicamento);
        return converterParaResponseDTO(medicamentoSalvo);
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarTodos() {
        List<Medicamento> medicamentos = medicamentoRepository.findAll();
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
        Medicamento existente = medicamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + id + " não encontrado para atualização."));

        // Verificação de código de barras duplicado (apenas se for alterado)
        if (requestDTO.getCodigoDeBarras() != null && !requestDTO.getCodigoDeBarras().isBlank() &&
                !requestDTO.getCodigoDeBarras().equals(existente.getCodigoDeBarras())) {
            if (medicamentoRepository.existsByCodigoDeBarras(requestDTO.getCodigoDeBarras())) {
                throw new IllegalArgumentException("Outro medicamento já existe com o código de barras: " + requestDTO.getCodigoDeBarras());
            }
            existente.setCodigoDeBarras(requestDTO.getCodigoDeBarras());
        }

        // Atualiza campos básicos
        existente.setNome(requestDTO.getNome());
        existente.setDescricao(requestDTO.getDescricao());
        existente.setValidade(requestDTO.getValidade());
        existente.setPrecoCompra(requestDTO.getPrecoCompra());
        existente.setPrecoVenda(requestDTO.getPrecoVenda());

        // Atualiza entidades relacionadas se IDs forem fornecidos
        if (requestDTO.getPrincipioAtivoId() != null) {
            PrincipioAtivo pa = principioAtivoRepository.findById(requestDTO.getPrincipioAtivoId())
                    .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + requestDTO.getPrincipioAtivoId() + " não encontrado."));
            existente.setPrincipioAtivo(pa);
        }

        if (requestDTO.getIndustriaId() != null) {
            Industria ind = industriaRepository.findById(requestDTO.getIndustriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + requestDTO.getIndustriaId() + " não encontrada."));
            existente.setIndustria(ind);
        } else {
            existente.setIndustria(null);
        }

        // --- LÓGICA DE PROMOÇÃO IMPLEMENTADA ---
        if (requestDTO.getPromocao() != null) {
            if (Boolean.TRUE.equals(requestDTO.getPromocao())) {
                // Regra 1: Não permitir duas promoções para o mesmo princípio ativo.
                if (medicamentoRepository.existsByPrincipioAtivoAndPromocaoIsTrueAndIdNot(existente.getPrincipioAtivo(), id)) {
                    throw new IllegalArgumentException("Já existe um medicamento com o mesmo princípio ativo ('" + existente.getPrincipioAtivo().getNome() + "') em promoção.");
                }

                // Regra 2: Garantir que o preço promocional é válido e respeita a margem de 10%.
                if (requestDTO.getPrecoPromocional() == null || requestDTO.getPrecoPromocional().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Preço promocional é obrigatório e deve ser positivo para ativar uma promoção.");
                }

                BigDecimal precoMinimoPromocao = existente.getPrecoCompra().multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.HALF_UP);
                if (requestDTO.getPrecoPromocional().compareTo(precoMinimoPromocao) < 0) {
                    throw new IllegalArgumentException("O preço promocional (R$ " + requestDTO.getPrecoPromocional() +
                            ") não pode ser inferior a 110% do preço de compra (R$ " + precoMinimoPromocao + ").");
                }

                existente.setPromocao(true);
                existente.setPrecoPromocional(requestDTO.getPrecoPromocional());

            } else { // Se requestDTO.getPromocao() for false
                existente.setPromocao(false);
                existente.setPrecoPromocional(null);
            }
        }

        Medicamento medicamentoAtualizado = medicamentoRepository.save(existente);
        return converterParaResponseDTO(medicamentoAtualizado);
    }

    @Transactional
    public void deletarMedicamento(Long id) {
        if (!medicamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Medicamento com ID " + id + " não encontrado para deleção.");
        }
        // TODO: Adicionar verificação se medicamento está em alguma Venda antes de deletar.
        medicamentoRepository.deleteById(id);
    }
}
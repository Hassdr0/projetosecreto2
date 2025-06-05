package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.*;
import materiamarcos.projetosecreto2.Model.Estoque;
import materiamarcos.projetosecreto2.Model.Farmacia;
import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import materiamarcos.projetosecreto2.Repository.EstoqueRepository;
import materiamarcos.projetosecreto2.Repository.FarmaciaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import materiamarcos.projetosecreto2.Repository.PrincipioAtivoRepository;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private FarmaciaRepository farmaciaRepository;

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;


    private EstoqueResponseDTO converterParaEstoqueResponseDTO(Estoque estoque) {
        if (estoque == null) return null;

        Medicamento med = estoque.getMedicamento();
        PrincipioAtivoResponseDTO paDTO = null;
        if (med.getPrincipioAtivo() != null) {
            paDTO = new PrincipioAtivoResponseDTO(med.getPrincipioAtivo().getId(), med.getPrincipioAtivo().getNome());
        }
        IndustriaDTO indDTO = null;
        if (med.getIndustria() != null) {
            indDTO = new IndustriaDTO(med.getIndustria().getId(), med.getIndustria().getNome(), med.getIndustria().getCnpj());
        }
        MedicamentoResponseDTO medDTO = new MedicamentoResponseDTO(
                med.getId(), med.getNome(), med.getDescricao(), med.getCodigoDeBarras(),
                med.getValidade(), med.getPrecoCompra(), med.getPrecoVenda(),
                med.isPromocao(), med.getPrecoPromocional(), paDTO, indDTO
        );

        Farmacia farm = estoque.getFarmacia();
        FarmaciaResponseDTO farmDTO = new FarmaciaResponseDTO(
                farm.getId(), farm.getNome(), farm.getEndereco(), farm.getCidade(),
                farm.getUf(), farm.getCnpj(), farm.getTelefone()
        );

        return new EstoqueResponseDTO(
                estoque.getId(),
                medDTO,
                farmDTO,
                estoque.getQuantidade(),
                estoque.getLote(),
                estoque.getDataDeValidadeDoLote(),
                estoque.getPrecoDeCustoDoLote(),
                estoque.getDataUltimaAtualizacao()
        );
    }


    @Transactional
    public EstoqueResponseDTO registrarEntradaEstoque(EntradaEstoqueRequestDTO requestDTO) {
        // 1. Buscar o Medicamento pelo ID
        Medicamento medicamento = medicamentoRepository.findById(requestDTO.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + requestDTO.getMedicamentoId() + " não encontrado."));

        // 2. Buscar a Farmácia (Filial) pelo ID
        Farmacia farmacia = farmaciaRepository.findById(requestDTO.getFarmaciaId())
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + requestDTO.getFarmaciaId() + " não encontrada."));

        // 3. Verificar se já existe um registro de estoque para este medicamento, farmácia e lote

        Estoque estoqueExistente = null;
        if (requestDTO.getLote() != null && !requestDTO.getLote().isBlank()) {
            estoqueExistente = estoqueRepository.findByMedicamentoAndFarmaciaAndLote(medicamento, farmacia, requestDTO.getLote())
                    .orElse(null);
        } else {

        }


        Estoque estoqueParaSalvar;
        if (estoqueExistente != null) {
            estoqueExistente.setQuantidade(estoqueExistente.getQuantidade() + requestDTO.getQuantidade());
            if (requestDTO.getDataDeValidadeDoLote() != null) {
                estoqueExistente.setDataDeValidadeDoLote(requestDTO.getDataDeValidadeDoLote());
            }
            if (requestDTO.getPrecoDeCustoDoLote() != null) {
                estoqueExistente.setPrecoDeCustoDoLote(requestDTO.getPrecoDeCustoDoLote());
            }
            estoqueParaSalvar = estoqueExistente;
        } else {

            estoqueParaSalvar = new Estoque();
            estoqueParaSalvar.setMedicamento(medicamento);
            estoqueParaSalvar.setFarmacia(farmacia);
            estoqueParaSalvar.setQuantidade(requestDTO.getQuantidade());
            estoqueParaSalvar.setLote(requestDTO.getLote());
            estoqueParaSalvar.setDataDeValidadeDoLote(requestDTO.getDataDeValidadeDoLote());
            estoqueParaSalvar.setPrecoDeCustoDoLote(requestDTO.getPrecoDeCustoDoLote());
        }


        Estoque estoqueSalvo = estoqueRepository.save(estoqueParaSalvar);
        return converterParaEstoqueResponseDTO(estoqueSalvo);
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> consultarEstoquePorFarmacia(Long farmaciaId) {
        Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + farmaciaId + " não encontrada."));

        List<Estoque> estoques = estoqueRepository.findByFarmacia(farmacia);
        if (estoques.isEmpty()) {
            return Collections.emptyList();
        }
        return estoques.stream()
                .map(this::converterParaEstoqueResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> consultarEstoquePorMedicamentoEFarmacia(Long medicamentoId, Long farmaciaId) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + medicamentoId + " não encontrado."));

        Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + farmaciaId + " não encontrada."));

        List<Estoque> estoques = estoqueRepository.findByMedicamentoAndFarmacia(medicamento, farmacia);
        if (estoques.isEmpty()) {

            return Collections.emptyList();
        }
        return estoques.stream()
                .map(this::converterParaEstoqueResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> consultarEstoquePorMedicamento(Long medicamentoId) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + medicamentoId + " não encontrado."));

        List<Estoque> estoques = estoqueRepository.findByMedicamento(medicamento);
        if (estoques.isEmpty()) {
            return Collections.emptyList();
        }
        return estoques.stream()
                .map(this::converterParaEstoqueResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void darBaixaEstoque(Long medicamentoId, Long farmaciaId, String lote, int quantidadeBaixa) throws EstoqueInsuficienteException {
        if (quantidadeBaixa <= 0) {
            throw new IllegalArgumentException("Quantidade para baixa deve ser positiva.");
        }

        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + medicamentoId + " não encontrado para baixa de estoque."));

        Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + farmaciaId + " não encontrada para baixa de estoque."));

        Estoque estoque;
        if (lote != null && !lote.isBlank()) {
            estoque = estoqueRepository.findByMedicamentoAndFarmaciaAndLote(medicamento, farmacia, lote)
                    .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para o medicamento ID " + medicamentoId + ", farmácia ID " + farmaciaId + " e lote '" + lote + "'."));
        } else {
            // Se não controlar por lote na baixa, ou se o lote não for especificado,
            // pode precisar de uma lógica para encontrar o registro de estoque apropriado.
            // Por simplicidade, vamos assumir que se o lote não é fornecido,
            // Em um sistema real, pode querer pegar o lote mais antigo (FIFO) ou um lote qualquer com saldo.
            List<Estoque> estoquesDisponiveis = estoqueRepository.findByMedicamentoAndFarmacia(medicamento, farmacia);
            if (estoquesDisponiveis.isEmpty()) {
                throw new EntityNotFoundException("Nenhum registro de estoque encontrado para o medicamento ID " + medicamentoId + " na farmácia ID " + farmaciaId + ".");
            }
            // Para este exemplo, vamos pegar o primeiro que tiver saldo suficiente, ou o primeiro da lista.
            // Esta lógica precisaria ser mais robusta em produção.
            estoque = estoquesDisponiveis.stream()
                    .filter(e -> e.getQuantidade() >= quantidadeBaixa)
                    .findFirst()
                    .orElse(estoquesDisponiveis.get(0));
        }

        if (estoque.getQuantidade() < quantidadeBaixa) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o medicamento '" + medicamento.getNome() +
                    (lote != null ? "' (Lote: " + lote + ")" : "") +
                    " na farmácia '" + farmacia.getNome() + "'. Disponível: " + estoque.getQuantidade() + ", Requerido: " + quantidadeBaixa);
        }

        estoque.setQuantidade(estoque.getQuantidade() - quantidadeBaixa);
        // dataUltimaAtualizacao será atualizada pelo @PreUpdate
        estoqueRepository.save(estoque);

        System.out.println("Baixa de " + quantidadeBaixa + " unidade(s) do medicamento '" + medicamento.getNome() +
                (lote != null ? "' (Lote: " + lote + ")" : "") +
                " na farmácia '" + farmacia.getNome() + "'. Novo saldo: " + estoque.getQuantidade());

        // TODO: Chamar verificação de estoque mínimo aqui
        // verificarEAlertarEstoqueMinimoPorPrincipioAtivo(medicamento.getPrincipioAtivo().getId(), farmacia.getId());
    }

    @Transactional(readOnly = true)
    public List<AlertaEstoqueDTO> verificarAlertasEstoqueMinimo() {
        List<AlertaEstoqueDTO> alertas = new ArrayList<>();
        List<PrincipioAtivo> todosPrincipiosAtivos = principioAtivoRepository.findAll();

        for (PrincipioAtivo pa : todosPrincipiosAtivos) {
            if (pa.getEstoqueMinimo() != null && pa.getEstoqueMinimo() > 0) {

                Integer quantidadeAtual = estoqueRepository.sumQuantidadeByPrincipioAtivoId(pa.getId());
                if (quantidadeAtual == null) {
                    quantidadeAtual = 0;
                }

                if (quantidadeAtual <= pa.getEstoqueMinimo()) {
                    alertas.add(new AlertaEstoqueDTO(
                            pa.getId(),
                            pa.getNome(),
                            quantidadeAtual,
                            pa.getEstoqueMinimo(),
                            "Estoque baixo para o princípio ativo."
                    ));

                    System.out.println("ALERTA: Estoque baixo para Princípio Ativo '" + pa.getNome() + "'. Atual: " + quantidadeAtual + ", Mínimo: " + pa.getEstoqueMinimo());
                }
            }
        }
        return alertas;
    }

    // TODO: Adicionar outros métodos de serviço para Estoque:
    // - darBaixaEstoque(Long medicamentoId, Long farmaciaId, String lote, int quantidadeBaixa)
    // - consultarEstoquePorMedicamentoEFarmacia(Long medicamentoId, Long farmaciaId) -> List<EstoqueResponseDTO> (pode ter vários lotes)
    // - consultarEstoqueTotalMedicamento(Long medicamentoId) -> int (soma de todas filiais/lotes)
    // - verificarEAlertarEstoqueMinimoPorPrincipioAtivo(Long principioAtivoId)
    // - realizarAjusteEstoque(AjusteEstoqueRequestDTO dto)

}
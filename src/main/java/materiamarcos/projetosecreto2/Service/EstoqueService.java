package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.EntradaEstoqueRequestDTO;
import materiamarcos.projetosecreto2.DTOs.EstoqueResponseDTO;
import materiamarcos.projetosecreto2.DTOs.FarmaciaResponseDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.IndustriaDTO;
import materiamarcos.projetosecreto2.Model.Estoque;
import materiamarcos.projetosecreto2.Model.Farmacia;
import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Repository.EstoqueRepository;
import materiamarcos.projetosecreto2.Repository.FarmaciaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    // Método Helper para converter Entidade Estoque para EstoqueResponseDTO
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
            // Se não existe, cria um novo registro de estoque
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

    // TODO: Adicionar outros métodos de serviço para Estoque:
    // - darBaixaEstoque(Long medicamentoId, Long farmaciaId, String lote, int quantidadeBaixa)
    // - consultarEstoquePorMedicamentoEFarmacia(Long medicamentoId, Long farmaciaId) -> List<EstoqueResponseDTO> (pode ter vários lotes)
    // - consultarEstoqueTotalMedicamento(Long medicamentoId) -> int (soma de todas filiais/lotes)
    // - verificarEAlertarEstoqueMinimoPorPrincipioAtivo(Long principioAtivoId)
    // - realizarAjusteEstoque(AjusteEstoqueRequestDTO dto)

}
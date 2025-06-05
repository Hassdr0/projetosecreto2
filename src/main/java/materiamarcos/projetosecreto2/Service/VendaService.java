package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.*;
import materiamarcos.projetosecreto2.Model.*;
import materiamarcos.projetosecreto2.Repository.*;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ItemVendaRepository itemVendaRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private BalconistaRepository balconistaRepository;
    @Autowired
    private FarmaciaRepository farmaciaRepository;
    @Autowired
    private EstoqueService estoqueService;

    @Transactional
    public VendaResponseDTO cancelarVenda(Long vendaId) throws EstoqueInsuficienteException {
        // 1. Busca a venda a ser cancelada
        Venda venda = vendaRepository.findById(vendaId) // <<--- PONTO DE FALHA POTENCIAL 1
                .orElseThrow(() -> new EntityNotFoundException("Venda com ID " + vendaId + " não encontrada."));

        // 2. Verifica se a venda já está cancelada
        if ("CANCELADA".equalsIgnoreCase(venda.getStatusVenda())) {
            throw new IllegalStateException("Esta venda já foi cancelada.");
        }

        // 3. Reverte o estoque para cada item da venda
        for (ItemVenda item : venda.getItens()) {
            AjusteEstoqueRequestDTO ajusteDTO = new AjusteEstoqueRequestDTO();
            ajusteDTO.setMedicamentoId(item.getMedicamento().getId());
            ajusteDTO.setFarmaciaId(venda.getFarmacia().getId());
            ajusteDTO.setQuantidadeAjuste(item.getQuantidade()); // Quantidade POSITIVA para devolver
            ajusteDTO.setMotivoAjuste("Cancelamento da Venda ID: " + vendaId);
            // Se você deu baixa de um lote específico na venda, precisará do lote aqui também.
            // ajusteDTO.setLote(item.getLote());

            estoqueService.ajustarEstoque(ajusteDTO); // <<--- PONTO DE FALHA POTENCIAL 2
        }

        // 4. Mudar o status da venda para "CANCELADA"
        venda.setStatusVenda("CANCELADA");
        venda.setObservacoes("Venda cancelada em " + LocalDateTime.now());
        Venda vendaCancelada = vendaRepository.save(venda);

        return converterParaVendaResponseDTO(vendaCancelada);
    }


    private VendaResponseDTO converterParaVendaResponseDTO(Venda venda) {
        if (venda == null) {
            return null;
        }

        // Converte a lista de ItemVenda
        List<ItemVendaResponseDTO> itensDTO = venda.getItens().stream()
                .map(item -> new ItemVendaResponseDTO(
                        item.getId(),
                        item.getMedicamento().getId(),
                        item.getMedicamento().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitarioVenda(),
                        item.getSubtotalItem()
                ))
                .collect(Collectors.toList());

        // Converte a entidade Cliente para ClienteResponseDTO
        ClienteResponseDTO clienteDTO = null;
        if (venda.getCliente() != null) {
            Cliente c = venda.getCliente();
            clienteDTO = new ClienteResponseDTO(
                    c.getId(), c.getNome(), c.getCpf(), c.getEmail(), c.getTelefone(),
                    c.getLogradouro(), c.getNumero(), c.getComplemento(), c.getBairro(),
                    c.getCidade(), c.getUf(), c.getCep()
            );
        }

        // Converte a entidade Balconista para BalconistaResponseDTO
        BalconistaResponseDTO balconistaDTO = null;
        if (venda.getBalconista() != null) {
            Balconista b = venda.getBalconista();
            balconistaDTO = new BalconistaResponseDTO(b.getId(), b.getNome(), b.getCpf(),
                    b.getTaxaComissao(), b.getSalarioBase());
        }

        // Converte a entidade Farmacia para FarmaciaResponseDTO
        FarmaciaResponseDTO farmaciaDTO = null;
        if (venda.getFarmacia() != null) {
            Farmacia f = venda.getFarmacia();
            farmaciaDTO = new FarmaciaResponseDTO(f.getId(), f.getNome(), f.getEndereco(),
                    f.getCidade(), f.getUf(), f.getCnpj(), f.getTelefone());
        }

        // Cria e retorna o VendaResponseDTO principal
        return new VendaResponseDTO(
                venda.getId(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getFormaPagamento(),
                venda.getStatusVenda(),
                venda.getObservacoes(),
                clienteDTO,
                balconistaDTO,
                farmaciaDTO,
                venda.getMotoboyEntrega(),
                venda.getEnderecoEntrega(),
                venda.getNumeroNotaFiscal(),
                itensDTO
        );
    }


    @Transactional
    public VendaResponseDTO registrarVenda(VendaRequestDTO requestDTO, String usernameBalconistaLogado) throws EstoqueInsuficienteException {
        // 1. Buscar Balconista
        Balconista balconista = balconistaRepository.findById(requestDTO.getBalconistaId())
                .orElseThrow(() -> new EntityNotFoundException("Balconista com ID " + requestDTO.getBalconistaId() + " não encontrado."));

        // Buscar Cliente (se clienteId for fornecido)
        Cliente cliente = null;
        if (requestDTO.getClienteId() != null) {
            cliente = clienteRepository.findById(requestDTO.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + requestDTO.getClienteId() + " não encontrado."));
        }

        //Buscar Farmácia
        Farmacia farmacia = farmaciaRepository.findById(requestDTO.getFarmaciaId())
                .orElseThrow(() -> new EntityNotFoundException("Farmácia com ID " + requestDTO.getFarmaciaId() + " não encontrada."));

        //Criar a entidade Venda
        Venda novaVenda = new Venda();
        novaVenda.setBalconista(balconista);
        novaVenda.setCliente(cliente); // Pode ser nulo
        novaVenda.setFarmacia(farmacia);
        novaVenda.setFormaPagamento(requestDTO.getFormaPagamento());
        novaVenda.setObservacoes(requestDTO.getObservacoes());
        novaVenda.setMotoboyEntrega(requestDTO.getMotoboyEntrega());
        novaVenda.setEnderecoEntrega(requestDTO.getEnderecoEntrega());


        BigDecimal valorTotalVenda = BigDecimal.ZERO;
        List<ItemVenda> listaItensVenda = new ArrayList<>();


        for (ItemVendaRequestDTO itemDTO : requestDTO.getItens()) {
            Medicamento medicamento = medicamentoRepository.findById(itemDTO.getMedicamentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + itemDTO.getMedicamentoId() + " não encontrado."));


            BigDecimal precoUnitario = medicamento.isPromocao() && medicamento.getPrecoPromocional() != null
                    ? medicamento.getPrecoPromocional()
                    : medicamento.getPrecoVenda();

            if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Preço de venda inválido para o medicamento: " + medicamento.getNome());
            }

            ItemVenda itemVenda = new ItemVenda(medicamento, itemDTO.getQuantidade(), precoUnitario);

            listaItensVenda.add(itemVenda);
            valorTotalVenda = valorTotalVenda.add(itemVenda.getSubtotalItem());

            estoqueService.darBaixaEstoque(medicamento.getId(), farmacia.getId(), null, itemDTO.getQuantidade());
        }

        novaVenda.setItens(listaItensVenda);
        for(ItemVenda iv : listaItensVenda) {
            iv.setVenda(novaVenda);
        }
        novaVenda.setValorTotal(valorTotalVenda);

        novaVenda.setNumeroNotaFiscal("NF" + System.currentTimeMillis()); //placeholder

        // TODO: Calcular comissão do balconista
        // BigDecimal comissaoCalculada = valorTotalVenda.multiply(balconista.getTaxaComissao());

        Venda vendaSalva = vendaRepository.save(novaVenda);
        return converterParaVendaResponseDTO(vendaSalva);
    }


    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodasVendas() {
        return vendaRepository.findAll()
                .stream()
                .map(this::converterParaVendaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarVendaPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda com ID " + id + " não encontrada."));
        return converterParaVendaResponseDTO(venda);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarVendasPorBalconista(Long balconistaId) {
        Balconista balconista = balconistaRepository.findById(balconistaId)
                .orElseThrow(() -> new EntityNotFoundException("Balconista com ID " + balconistaId + " não encontrado."));

        List<Venda> vendas = vendaRepository.findByBalconista(balconista);
        return vendas.stream()
                .map(this::converterParaVendaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarVendasPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + clienteId + " não encontrado."));

        List<Venda> vendas = vendaRepository.findByCliente(cliente);
        return vendas.stream()
                .map(this::converterParaVendaResponseDTO)
                .collect(Collectors.toList());
    }
}
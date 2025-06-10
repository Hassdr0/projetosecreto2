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
import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private CotacaoRepository cotacaoRepository;
    @Autowired
    private EstoqueService estoqueService;

    // o ideal é ter um helper aqui

    @Transactional
    public Compra criarCompraDeMelhorCotacao(CompraRequestDTO requestDTO) {
        Medicamento medicamento = medicamentoRepository.findById(requestDTO.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento com ID " + requestDTO.getMedicamentoId() + " não encontrado."));

        //Buscar cotações e pegar a de menor preço
        List<Cotacao> cotacoes = cotacaoRepository.findByMedicamentoIdAndAtivaTrueOrderByPrecoUnitarioCotadoAsc(requestDTO.getMedicamentoId());

        if (cotacoes.isEmpty()) {
            throw new RuntimeException("Nenhuma cotação ativa encontrada para o medicamento: " + medicamento.getNome());
        }

        if (cotacoes.size() < 3) {
       throw new RuntimeException("É necessário ter pelo menos 3 cotações para realizar a compra.");
         }

        Cotacao melhorCotacao = cotacoes.get(0);

        Compra novaCompra = new Compra();
        novaCompra.setFornecedor(melhorCotacao.getIndustria());
        novaCompra.setDataCompra(LocalDate.now());
        novaCompra.setStatus("PENDENTE_RECEBIMENTO");

        ItemCompra itemCompra = new ItemCompra();
        itemCompra.setMedicamento(medicamento);
        itemCompra.setQuantidade(requestDTO.getQuantidade());
        itemCompra.setPrecoUnitarioComprado(melhorCotacao.getPrecoUnitarioCotado());
        itemCompra.calcularSubtotal();

        novaCompra.addItem(itemCompra); // Adiciona o item à compra

        // Calcula o valor total da compra
        novaCompra.setValorTotal(itemCompra.getSubtotal());

        return compraRepository.save(novaCompra);
    }

    @Transactional
    public Compra confirmarRecebimentoCompra(Long compraId, ConfirmarRecebimentoCompraDTO requestDTO) throws EstoqueInsuficienteException {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new EntityNotFoundException("Compra com ID " + compraId + " não encontrada."));

        if (!"PENDENTE_RECEBIMENTO".equalsIgnoreCase(compra.getStatus())) {
            throw new IllegalStateException("Esta compra não pode ser recebida pois seu status é: " + compra.getStatus());
        }

        compra.setNumeroNotaFiscal(requestDTO.getNumeroNotaFiscal());
        compra.setStatus("RECEBIDA");


        for (ItemCompra item : compra.getItens()) {
            EntradaEstoqueRequestDTO entradaDTO = new EntradaEstoqueRequestDTO();
            entradaDTO.setMedicamentoId(item.getMedicamento().getId());
            entradaDTO.setFarmaciaId(1L); // <<<--- ATENÇÃO: ID da farmácia fixo para exemplo!
            entradaDTO.setQuantidade(item.getQuantidade());
            entradaDTO.setPrecoDeCustoDoLote(item.getPrecoUnitarioComprado());
            entradaDTO.setLote("LOTE_DA_NF");
            entradaDTO.setDataDeValidadeDoLote(LocalDate.of(2028,1,1));

            estoqueService.registrarEntradaEstoque(entradaDTO);
        }

        return compraRepository.save(compra);
    }
}
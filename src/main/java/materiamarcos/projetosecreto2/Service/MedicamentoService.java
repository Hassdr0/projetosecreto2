package materiamarcos.projetosecreto2.Service; // Ajuste o pacote se necessário

import materiamarcos.projetosecreto2.DTOs.MedicamentoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.MedicamentoResponseDTO;
import materiamarcos.projetosecreto2.DTOs.PrincipioAtivoDTO; // Precisaremos para o ResponseDTO
import materiamarcos.projetosecreto2.DTOs.IndustriaDTO;   // Precisaremos para o ResponseDTO
import materiamarcos.projetosecreto2.Model.Industria;
import materiamarcos.projetosecreto2.Model.Medicamento;
import materiamarcos.projetosecreto2.Model.PrincipioAtivo;
import materiamarcos.projetosecreto2.Repository.IndustriaRepository;
import materiamarcos.projetosecreto2.Repository.MedicamentoRepository;
import materiamarcos.projetosecreto2.Repository.PrincipioAtivoRepository;
import jakarta.persistence.EntityNotFoundException; // Para lançar se algo não for encontrado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    @Autowired
    private IndustriaRepository industriaRepository;

    @Transactional // Garante que todas as operações de banco de dados sejam atômicas
    public MedicamentoResponseDTO criarMedicamento(MedicamentoRequestDTO requestDTO) {
        //Verificar se já existe um medicamento com o mesmo código de barras (se for um campo único)
        if (requestDTO.getCodigoDeBarras() != null && !requestDTO.getCodigoDeBarras().isBlank()) {
            if (medicamentoRepository.existsByCodigoDeBarras(requestDTO.getCodigoDeBarras())) {
                throw new IllegalArgumentException("Medicamento com o código de barras '" + requestDTO.getCodigoDeBarras() + "' já existe.");
            }
        }

        //Buscar o Princípio Ativo pelo ID fornecido no DTO
        PrincipioAtivo principioAtivo = principioAtivoRepository.findById(requestDTO.getPrincipioAtivoId())
                .orElseThrow(() -> new EntityNotFoundException("Princípio Ativo com ID " + requestDTO.getPrincipioAtivoId() + " não encontrado."));

        //Buscar a Indústria pelo ID fornecido no DTO
        Industria industria = null; // Indústria pode ser opcional
        if (requestDTO.getIndustriaId() != null) {
            industria = industriaRepository.findById(requestDTO.getIndustriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Indústria com ID " + requestDTO.getIndustriaId() + " não encontrada."));
        }

        // Criar a nova entidade Medicamento
        Medicamento novoMedicamento = new Medicamento();
        novoMedicamento.setNome(requestDTO.getNome());
        novoMedicamento.setDescricao(requestDTO.getDescricao());
        novoMedicamento.setCodigoDeBarras(requestDTO.getCodigoDeBarras());
        novoMedicamento.setValidade(requestDTO.getValidade());
        novoMedicamento.setPrecoCompra(requestDTO.getPrecoCompra());
        novoMedicamento.setPrecoVenda(requestDTO.getPrecoVenda());

        // Associar as entidades encontradas
        novoMedicamento.setPrincipioAtivo(principioAtivo);
        if (industria != null) {
            novoMedicamento.setIndustria(industria);
        }

        // Lógica de promoção
        if (requestDTO.getPromocao() != null) {
            novoMedicamento.setPromocao(requestDTO.getPromocao());
            if (requestDTO.getPromocao() && requestDTO.getPrecoPromocional() != null) {
                // TODO: Adicionar validação da regra de negócio da promoção aqui
                // (preço promocional >= preço de compra + 10%)
                novoMedicamento.setPrecoPromocional(requestDTO.getPrecoPromocional());
            } else {
                novoMedicamento.setPrecoPromocional(null); // Garante que não haja preço promocional se não estiver em promoção
            }
        }


        // Salvar o medicamento no banco de dados
        Medicamento medicamentoSalvo = medicamentoRepository.save(novoMedicamento);

        // Converter a entidade salva para MedicamentoResponseDTO e retornar
        return converterParaResponseDTO(medicamentoSalvo);
    }

    // Método helper privado para converter Entidade para DTO
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
}
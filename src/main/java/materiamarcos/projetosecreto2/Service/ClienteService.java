package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.DTOs.ClienteRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ClienteResponseDTO;
import materiamarcos.projetosecreto2.Model.Cliente;
import materiamarcos.projetosecreto2.Repository.ClienteRepository;
// import materiamarcos.projetosecreto2.Repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // @Autowired
    // private VendaRepository vendaRepository;

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getEmail(),
                cliente.getTelefone(), cliente.getLogradouro(), cliente.getNumero(),
                cliente.getComplemento(), cliente.getBairro(), cliente.getCidade(),
                cliente.getUf(), cliente.getCep()
        );
    }

    private String normalizeNumeric(String value) {
        if (value == null) return null;
        return value.replaceAll("[^0-9]", "");
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO requestDTO) {
        if (StringUtils.hasText(requestDTO.getCpf()) && clienteRepository.existsByCpf(requestDTO.getCpf())) {
            throw new IllegalArgumentException("Cliente com o CPF '" + requestDTO.getCpf() + "' já existe.");
        }
        if (StringUtils.hasText(requestDTO.getEmail()) && clienteRepository.existsByEmailIgnoreCase(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Cliente com o Email '" + requestDTO.getEmail() + "' já existe.");
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(requestDTO.getNome());
        novoCliente.setCpf(requestDTO.getCpf());
        novoCliente.setEmail(requestDTO.getEmail());
        novoCliente.setTelefone(requestDTO.getTelefone());
        novoCliente.setLogradouro(requestDTO.getLogradouro());
        novoCliente.setNumero(requestDTO.getNumero());
        novoCliente.setComplemento(requestDTO.getComplemento());
        novoCliente.setBairro(requestDTO.getBairro());
        novoCliente.setCidade(requestDTO.getCidade());
        novoCliente.setUf(requestDTO.getUf());
        novoCliente.setCep(requestDTO.getCep());

        Cliente salvo = clienteRepository.save(novoCliente);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));
        return toResponseDTO(cliente);
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO requestDTO) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado para atualização."));

        if (StringUtils.hasText(requestDTO.getCpf())) {
            String novoCpf = requestDTO.getCpf();
            if (!novoCpf.equals(existente.getCpf()) &&
                    clienteRepository.findByCpf(novoCpf).filter(c -> !c.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Outro cliente com o CPF '" + requestDTO.getCpf() + "' já existe.");
            }
            existente.setCpf(novoCpf);
        } else {
            existente.setCpf(null);
        }

        if (StringUtils.hasText(requestDTO.getEmail())) {
            String novoEmail = requestDTO.getEmail();
            if (existente.getEmail() == null || !novoEmail.equalsIgnoreCase(existente.getEmail())) {
                if(clienteRepository.findByEmailIgnoreCase(novoEmail).filter(c -> !c.getId().equals(id)).isPresent()){
                    throw new IllegalArgumentException("Outro cliente com o Email '" + requestDTO.getEmail() + "' já existe.");
                }
            }
            existente.setEmail(novoEmail);
        } else {
            existente.setEmail(null);
        }

        existente.setNome(requestDTO.getNome());
        existente.setTelefone(requestDTO.getTelefone());
        existente.setLogradouro(requestDTO.getLogradouro());
        existente.setNumero(requestDTO.getNumero());
        existente.setComplemento(requestDTO.getComplemento());
        existente.setBairro(requestDTO.getBairro());
        existente.setCidade(requestDTO.getCidade());
        existente.setUf(requestDTO.getUf());
        existente.setCep(requestDTO.getCep());

        Cliente atualizado = clienteRepository.save(existente);
        return toResponseDTO(atualizado);
    }

    @Transactional
    public void deletarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado para deleção."));

        // TODO: Adicionar verificação se o cliente tem Vendas associadas antes de deletar.

        // if (vendaRepository.existsByClienteId(id)) {
        //     throw new DataIntegrityViolationException("Não é possível deletar o cliente '" + cliente.getNome() + "' pois ele possui vendas associadas.");
        // }

        clienteRepository.delete(cliente);
    }
}
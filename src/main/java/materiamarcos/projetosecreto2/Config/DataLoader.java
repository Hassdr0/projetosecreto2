package materiamarcos.projetosecreto2.Config;

import materiamarcos.projetosecreto2.Model.*;
import materiamarcos.projetosecreto2.Model.enums.ERole;
import materiamarcos.projetosecreto2.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    // --- Injeções de Dependência ---
    @Autowired private UserRepository usuarioRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private BalconistaRepository balconistaRepository;
    @Autowired private ClienteRepository clienteRepository; // Certifique-se que esta injeção existe
    @Autowired private PrincipioAtivoRepository principioAtivoRepository;
    @Autowired private IndustriaRepository industriaRepository;
    @Autowired private FarmaciaRepository farmaciaRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private EstoqueRepository estoqueRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Roda apenas se o banco estiver vazio para evitar duplicatas
        if (usuarioRepository.count() > 0) {
            System.out.println("DataLoader: Dados já existem no banco. Pulando carregamento inicial.");
            return;
        }

        System.out.println("Iniciando DataLoader: Populando banco de dados...");

        // 1. Criar Papéis (Roles)
        Role roleAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));
        Role roleBalconista = roleRepository.save(new Role(ERole.ROLE_BALCONISTA));
        Role roleCliente = roleRepository.save(new Role(ERole.ROLE_CLIENTE));
        System.out.println("-> Papéis (Roles) criados.");

        // 2. Criar Usuários com Papéis
        User adminUser = new User("admin", "admin@barateira.com", passwordEncoder.encode("admin123"));
        adminUser.addRole(roleAdmin);
        usuarioRepository.save(adminUser);

        User balconistaUser = new User("joao.atendente", "joao@barateira.com", passwordEncoder.encode("joao123"));
        balconistaUser.addRole(roleBalconista);
        usuarioRepository.save(balconistaUser);

        User clienteUser = new User("carlos.fiel", "carlos@email.com", passwordEncoder.encode("carlos123"));
        clienteUser.addRole(roleCliente);
        usuarioRepository.save(clienteUser);
        System.out.println("-> Usuários de exemplo criados com papéis.");

        // 3. Criar Balconistas e Clientes
        balconistaRepository.save(new Balconista(null, "João Atendente", "111.111.111-11", new BigDecimal("0.05"), new BigDecimal("1500.00"), balconistaUser));
        balconistaRepository.save(new Balconista(null, "Maria Vendedora", "222.222.222-22", new BigDecimal("0.06"), new BigDecimal("1550.00"), null));

        // CORREÇÃO: Usando a variável 'clienteRepository' (minúsculo) para chamar o método de instância '.save()'
        clienteRepository.save(new Cliente(null, "Carlos Cliente Fiel", "444.444.444-44", "carlos@email.com", "11999997777", "Rua das Flores", "100", "Apto 1", "Jardim", "Cidade Bela", "SP", "12345-001"));
        clienteRepository.save(new Cliente(null, "Ana Testadora", "555.555.555-55", "ana.teste@email.com", "22988887777", "Av. Principal", "200", null, "Centro", "Outra Cidade", "RJ", "23456-002"));
        System.out.println("-> Balconistas e Clientes de exemplo criados.");

        // 4. Criar outras entidades de apoio
        PrincipioAtivo paParacetamol = principioAtivoRepository.save(new PrincipioAtivo(null, "Paracetamol", 20));
        Industria indNeoQuimica = industriaRepository.save(new Industria(null, "Neo Química", "11.111.111/0001-11"));
        Farmacia farmaciaCentro = farmaciaRepository.save(new Farmacia(null, "Barateira Filial Centro", "Rua Principal, 123", "Cidade Central", "SP", "22.222.222/0001-22", "1155554444"));

        // 5. Criar Medicamentos e Estoque inicial
        Medicamento medTylenol = medicamentoRepository.save(new Medicamento(null, "Tylenol 750mg", "Analgésico", "7890001001", LocalDate.of(2027, 1, 1), new BigDecimal("6.00"), new BigDecimal("14.50"), false, null, paParacetamol, indNeoQuimica));
        estoqueRepository.save(new Estoque(null, medTylenol, farmaciaCentro, 100, "LOTE_TYL_2027", LocalDate.of(2027, 1, 1), new BigDecimal("6.00"), null));
        System.out.println("-> Medicamentos e Estoques iniciais criados.");

        System.out.println("DataLoader finalizado com sucesso.");
    }
}
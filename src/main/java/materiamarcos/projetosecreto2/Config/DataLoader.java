package materiamarcos.projetosecreto2.Config;

import materiamarcos.projetosecreto2.Model.*;
import materiamarcos.projetosecreto2.Repository.*;
// Adicione RoleRepository e ERole se for usar papéis para o usuário
// import materiamarcos.projetosecreto2.Model.enums.ERole;
// import materiamarcos.projetosecreto2.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
// import java.util.HashSet; // Para roles
// import java.util.Set; // Para roles

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    // @Autowired private RoleRepository roleRepository; // Descomente se for usar Roles
    @Autowired private PrincipioAtivoRepository principioAtivoRepository;
    @Autowired private IndustriaRepository industriaRepository;
    @Autowired private FarmaciaRepository farmaciaRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private EstoqueRepository estoqueRepository;
    @Autowired private BalconistaRepository balconistaRepository; // Nova Injeção
    @Autowired private ClienteRepository clienteRepository;       // Nova Injeção

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Iniciando DataLoader: Verificando e carregando dados iniciais...");

        // Criação de Papéis (Roles)
        /*
        if (roleRepository.count() == 0) {
            Arrays.stream(ERole.values()).forEach(roleEnum -> {
                if (roleRepository.findByNome(roleEnum).isEmpty()) {
                    roleRepository.save(new Role(roleEnum));
                }
            });
            System.out.println("Papéis (Roles) criados/verificados.");
        }
        Role roleAdmin = roleRepository.findByNome(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("ROLE_ADMIN não encontrado"));
        Role roleBalconista = roleRepository.findByNome(ERole.ROLE_BALCONISTA).orElseThrow(() -> new RuntimeException("ROLE_BALCONISTA não encontrado"));
        Role roleCliente = roleRepository.findByNome(ERole.ROLE_CLIENTE).orElseThrow(() -> new RuntimeException("ROLE_CLIENTE não encontrado"));
        */

        // --- Criar Usuário Admin ---
        User adminUser = null;
        if (!usuarioRepository.existsByUsername("admin")) {
            adminUser = new User("admin", "admin@barateira.com", passwordEncoder.encode("admin123"));
            // Set<Role> adminRoles = new HashSet<>();
            // adminRoles.add(roleAdmin);
            // adminUser.setRoles(adminRoles);
            usuarioRepository.save(adminUser);
            System.out.println("Usuário 'admin' criado.");
        } else {
            adminUser = usuarioRepository.findByUsername("admin").orElse(null);
        }

        // --- Criar Balconistas ---
        Balconista balconistaJoao = null;
        if (!balconistaRepository.existsByCpf("111.111.111-11")) {
            balconistaJoao = new Balconista(null, "João Atendente", "111.111.111-11", new BigDecimal("0.05"), new BigDecimal("1500.00"), null /* user */);
            // Se quiser vincular ao User admin (exemplo, não ideal para um balconista real):
            // if (adminUser != null) balconistaJoao.setUser(adminUser);
            balconistaRepository.save(balconistaJoao);
            System.out.println("Balconista 'João Atendente' criado.");
        } else {
            balconistaJoao = balconistaRepository.findByCpf("111.111.111-11").orElse(null);
        }

        Balconista balconistaMaria = null;
        if (!balconistaRepository.existsByCpf("222.222.222-22")) {
            balconistaMaria = new Balconista(null, "Maria Vendedora", "222.222.222-22", new BigDecimal("0.06"), new BigDecimal("1550.00"), null /* user */);
            balconistaRepository.save(balconistaMaria);
            System.out.println("Balconista 'Maria Vendedora' criada.");
        } else {
            balconistaMaria = balconistaRepository.findByCpf("222.222.222-22").orElse(null);
        }


        // --- Criar Clientes ---
        Cliente clienteCarlos = null;
        if (!clienteRepository.existsByCpf("333.333.333-33")) {
            clienteCarlos = new Cliente(null, "Carlos Cliente Fiel", "333.333.333-33", "carlos@email.com", "11999997777", "Rua das Flores", "100", "Apto 1", "Jardim", "Cidade Bela", "SP", "12345-001");
            clienteRepository.save(clienteCarlos);
            System.out.println("Cliente 'Carlos Cliente Fiel' criado.");
        } else {
            clienteCarlos = clienteRepository.findByCpf("333.333.333-33").orElse(null);
        }

        Cliente clienteAna = null;
        if (!clienteRepository.existsByEmailIgnoreCase("ana.teste@email.com")) {
            clienteAna = new Cliente(null, "Ana Testadora", null, "ana.teste@email.com", "22988887777", "Av. Principal", "200", null, "Centro", "Outra Cidade", "RJ", "23456-002");
            clienteRepository.save(clienteAna);
            System.out.println("Cliente 'Ana Testadora' criada.");
        } else {
            clienteAna = clienteRepository.findByEmailIgnoreCase("ana.teste@email.com").orElse(null);
        }


        // --- Criar Princípios Ativos ---
        PrincipioAtivo paParacetamol = criarOuCarregarPrincipioAtivo("Paracetamol", 15);
        PrincipioAtivo paDipirona = criarOuCarregarPrincipioAtivo("Dipirona Sódica", null); // Sem estoque mínimo definido
        PrincipioAtivo paIbuprofeno = criarOuCarregarPrincipioAtivo("Ibuprofeno", 20);

        // --- Criar Indústrias ---
        Industria indMedley = criarOuCarregarIndustria("Medley Farmacêutica", "11.111.111/0001-11");
        Industria indNeoQuimica = criarOuCarregarIndustria("Neo Química", "22.222.222/0001-22");
        Industria indEMS = criarOuCarregarIndustria("EMS Pharma", "33.333.333/0001-33");


        // --- Criar Farmácias (Filiais) ---
        Farmacia farmaciaCentro = criarOuCarregarFarmacia("Barateira Filial Centro", "33.333.333/0001-33", "Rua Principal, 123", "Cidade Central", "SP", "1133334444");
        Farmacia farmaciaBairro = criarOuCarregarFarmacia("Barateira Filial Bairro Feliz", "44.444.444/0001-44", "Av. Secundária, 456", "Cidade Central", "SP", "1155556666");


        // --- Criar Medicamentos
        if (medicamentoRepository.count() == 0) {
            if (paParacetamol != null && indNeoQuimica != null) {
                Medicamento medTylenol = new Medicamento(null, "Tylenol 750mg", "Analgésico e antitérmico", "7890001001",
                        LocalDate.of(2026, 12, 31), new BigDecimal("5.50"), new BigDecimal("12.80"),
                        false, null, paParacetamol, indNeoQuimica);
                medicamentoRepository.save(medTylenol);
                System.out.println("Medicamento Tylenol 750mg criado.");
                // Adicionar estoque para Tylenol
                if (farmaciaCentro != null) criarOuAtualizarEstoque(medTylenol, farmaciaCentro, 5, "LOTE_TYL001", LocalDate.of(2026,12,31), new BigDecimal("5.50")); // Estoque baixo
                if (farmaciaBairro != null) criarOuAtualizarEstoque(medTylenol, farmaciaBairro, 5, "LOTE_TYL002", LocalDate.of(2026,12,31), new BigDecimal("5.50")); // Estoque baixo
            }

            if (paDipirona != null && indMedley != null) {
                Medicamento medDorflex = new Medicamento(null, "Dorflex", "Relaxante muscular e analgésico", "7890001002",
                        LocalDate.of(2027, 10, 15), new BigDecimal("3.20"), new BigDecimal("7.50"),
                        false, null, paDipirona, indMedley);
                medicamentoRepository.save(medDorflex);
                System.out.println("Medicamento Dorflex criado.");
                if (farmaciaCentro != null) criarOuAtualizarEstoque(medDorflex, farmaciaCentro, 150, "LOTE_DORF001", LocalDate.of(2027,10,15), new BigDecimal("3.20"));
            }

            if (paIbuprofeno != null && indEMS != null) {
                Medicamento medAlivium = new Medicamento(null, "Alivium 400mg", "Anti-inflamatório", "7890001003",
                        LocalDate.of(2026, 8, 20), new BigDecimal("8.00"), new BigDecimal("15.00"),
                        false, null, paIbuprofeno, indEMS);
                medicamentoRepository.save(medAlivium);
                System.out.println("Medicamento Alivium criado.");
                if (farmaciaCentro != null) criarOuAtualizarEstoque(medAlivium, farmaciaCentro, 25, "LOTE_ALIV001", LocalDate.of(2026,8,20), new BigDecimal("8.00")); // Estoque para alerta
            }
            System.out.println("Medicamentos e estoques iniciais carregados.");
        } else {
            System.out.println("Medicamentos já existentes. Verifique o estoque manualmente se necessário.");
        }

        System.out.println("DataLoader finalizado.");
    }

    // Métodos helper para evitar duplicação e garantir que só cria se não existir
    private PrincipioAtivo criarOuCarregarPrincipioAtivo(String nome, Integer estoqueMinimo) {
        return principioAtivoRepository.findByNomeIgnoreCase(nome).map(pa -> {
            boolean atualizado = false;
            if (estoqueMinimo != null && (pa.getEstoqueMinimo() == null || !pa.getEstoqueMinimo().equals(estoqueMinimo))) {
                pa.setEstoqueMinimo(estoqueMinimo);
                atualizado = true;
            }
            if (atualizado) {
                System.out.println("Princípio Ativo '" + nome + "' atualizado.");
                return principioAtivoRepository.save(pa);
            }
            return pa;
        }).orElseGet(() -> {
            PrincipioAtivo novoPa = new PrincipioAtivo(nome);
            if (estoqueMinimo != null) {
                novoPa.setEstoqueMinimo(estoqueMinimo);
            }
            System.out.println("Princípio Ativo '" + nome + "' criado" + (estoqueMinimo != null ? " com estoque mínimo " + estoqueMinimo : "") + ".");
            return principioAtivoRepository.save(novoPa);
        });
    }

    private Industria criarOuCarregarIndustria(String nome, String cnpj) {
        String cnpjNormalizado;
        if(StringUtils.hasText(cnpj)) cnpjNormalizado = cnpj.replaceAll("[^0-9]", "");
        else {
            cnpjNormalizado = null;
        }

        // Tenta buscar pelo CNPJ primeiro se informado
        if (StringUtils.hasText(cnpjNormalizado)) {
            return industriaRepository.findByCnpj(cnpjNormalizado).orElseGet(() -> {
                System.out.println("Indústria '" + nome + "' criada.");
                return industriaRepository.save(new Industria(nome, cnpjNormalizado)); // Salva normalizado
            });
        }
        // Se CNPJ não informado, tenta pelo nome
        return industriaRepository.findByNomeIgnoreCase(nome).orElseGet(() -> {
            System.out.println("Indústria '" + nome + "' criada (sem CNPJ).");
            return industriaRepository.save(new Industria(nome, null));
        });
    }

    private Farmacia criarOuCarregarFarmacia(String nome, String cnpj, String endereco, String cidade, String uf, String telefone) {
        String cnpjNormalizado;
        if(StringUtils.hasText(cnpj)) cnpjNormalizado = cnpj.replaceAll("[^0-9]", "");
        else {
            cnpjNormalizado = null;
        }

        return farmaciaRepository.findByNomeIgnoreCase(nome).orElseGet(()-> {
            System.out.println("Farmácia '" + nome + "' criada.");
            return farmaciaRepository.save(new Farmacia(null, nome, endereco, cidade, uf, cnpjNormalizado, telefone));
        });
    }

    // --- MÉTODO CORRIGIDO ---
    private void criarOuAtualizarEstoque(Medicamento medicamento, Farmacia farmacia, int quantidade, String lote, LocalDate validadeLote, BigDecimal precoCusto) {
        if (medicamento == null || farmacia == null) {
            System.err.println("Não foi possível criar/atualizar estoque: Medicamento ou Farmácia nulos.");
            return;
        }

        Estoque estoque;
        if (StringUtils.hasText(lote)) {
            estoque = estoqueRepository.findByMedicamentoAndFarmaciaAndLote(medicamento, farmacia, lote)
                    .orElse(null);
        } else {
            estoque = estoqueRepository.findByMedicamentoAndFarmacia(medicamento, farmacia).stream()
                    .filter(e -> !StringUtils.hasText(e.getLote()))
                    .findFirst()
                    .orElse(null);
        }

        if (estoque == null) {
            estoque = new Estoque(null, medicamento, farmacia, quantidade, StringUtils.hasText(lote) ? lote : null, validadeLote, precoCusto, null);
            System.out.println("Criando novo registro de estoque para " + medicamento.getNome() + " na " + farmacia.getNome() + (StringUtils.hasText(lote) ? " Lote: " + lote : " (sem lote)") + " com Qtd: " + quantidade);
        } else {
            estoque.setQuantidade(estoque.getQuantidade() + quantidade);
            if (validadeLote != null) estoque.setDataDeValidadeDoLote(validadeLote);
            if (precoCusto != null) estoque.setPrecoDeCustoDoLote(precoCusto);
            System.out.println("Atualizando estoque para " + medicamento.getNome() + " na " + farmacia.getNome() + (StringUtils.hasText(lote) ? " Lote: " + lote : " (sem lote)") + ". Nova Qtd: " + estoque.getQuantidade());
        }
        estoqueRepository.save(estoque);
    }
}
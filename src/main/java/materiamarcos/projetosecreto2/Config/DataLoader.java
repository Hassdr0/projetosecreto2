package materiamarcos.projetosecreto2.Config;

import materiamarcos.projetosecreto2.Model.*;
import materiamarcos.projetosecreto2.Model.Role;
import materiamarcos.projetosecreto2.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import materiamarcos.projetosecreto2.Model.enums.ERole;
import java.util.Set;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PrincipioAtivoRepository principioAtivoRepository;
    @Autowired private IndustriaRepository industriaRepository;
    @Autowired private FarmaciaRepository farmaciaRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private EstoqueRepository estoqueRepository;
    @Autowired private BalconistaRepository balconistaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private roleRepository roleRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() > 0) {
            System.out.println("DataLoader: Dados já existem no banco. Pulando carregamento inicial.");
            return;
        }

        System.out.println("Iniciando DataLoader: Populando o banco de dados com dados iniciais...");

        // 1. Criar Papéis (Roles)
        roleRepository.save(new Role(ERole.ROLE_ADMIN));
        roleRepository.save(new Role(ERole.ROLE_BALCONISTA));
        roleRepository.save(new Role(ERole.ROLE_CLIENTE));
        System.out.println("-> Papéis criados.");

        Role adminRole = materiamarcos.projetosecreto2.Repository.roleRepository.findByNome(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Erro: Papel Admin não encontrado."));
        Role balconistaRole = materiamarcos.projetosecreto2.Repository.roleRepository.findByNome(ERole.ROLE_BALCONISTA).orElseThrow(() -> new RuntimeException("Erro: Papel Balconista não encontrado."));

        // 2. Criar Usuários com Papéis
        User adminUser = new User("admin", "admin@barateira.com", passwordEncoder.encode("admin123"));
        adminUser.setRoles(Set.of(adminRole)); // Atribui o papel de Admin
        usuarioRepository.save(adminUser);

        User balconistaUser = new User("joao.atendente", "joao@barateira.com", passwordEncoder.encode("joao123"));
        balconistaUser.setRoles(Set.of(balconistaRole)); // Atribui o papel de Balconista
        usuarioRepository.save(balconistaUser);
        System.out.println("-> Usuários 'admin' e 'joao.atendente' criados com seus papéis.");

        // 2. Criar Princípios Ativos e Indústrias
        PrincipioAtivo paParacetamol = criarOuCarregarPrincipioAtivo("Paracetamol", 15);
        PrincipioAtivo paDipirona = criarOuCarregarPrincipioAtivo("Dipirona Sódica", null);
        PrincipioAtivo paIbuprofeno = criarOuCarregarPrincipioAtivo("Ibuprofeno", 20);

        Industria indMedley = criarOuCarregarIndustria("Medley Farmacêutica", "11.111.111/0001-11");
        Industria indNeoQuimica = criarOuCarregarIndustria("Neo Química", "22.222.222/0001-22");
        Industria indEMS = criarOuCarregarIndustria("EMS Pharma", "33.333.333/0001-33");

        // 3. Criar Farmácias (Filiais)
        Farmacia farmaciaCentro = criarOuCarregarFarmacia("Barateira Filial Centro", "66666666000166", "Rua Principal, 123", "Cidade Central", "SP", "1133334444");
        Farmacia farmaciaBairro = criarOuCarregarFarmacia("Barateira Filial Bairro Feliz", "77777777000177", "Av. Secundária, 456", "Cidade Central", "SP", "1155556666");

        // 4. Criar Medicamentos e Estoque Inicial
        if (medicamentoRepository.count() == 0) {
            Medicamento medTylenol = medicamentoRepository.save(new Medicamento(null, "Tylenol 750mg", "Analgésico e antitérmico", "7890001001", LocalDate.of(2026, 12, 31), new BigDecimal("5.50"), new BigDecimal("12.80"), false, null, paParacetamol, indNeoQuimica));
            criarOuAtualizarEstoque(medTylenol, farmaciaCentro, 5, "LOTE_TYL001", LocalDate.of(2026,12,31), new BigDecimal("5.50"));
            criarOuAtualizarEstoque(medTylenol, farmaciaBairro, 5, "LOTE_TYL002", LocalDate.of(2026,12,31), new BigDecimal("5.50"));

            Medicamento medDorflex = medicamentoRepository.save(new Medicamento(null, "Dorflex", "Relaxante muscular", "7890001002", LocalDate.of(2027, 10, 15), new BigDecimal("3.20"), new BigDecimal("7.50"), false, null, paDipirona, indMedley));
            criarOuAtualizarEstoque(medDorflex, farmaciaCentro, 150, "LOTE_DORF001", LocalDate.of(2027,10,15), new BigDecimal("3.20"));

            Medicamento medAlivium = medicamentoRepository.save(new Medicamento(null, "Alivium 400mg", "Anti-inflamatório", "7890001003", LocalDate.of(2026, 8, 20), new BigDecimal("8.00"), new BigDecimal("15.00"), false, null, paIbuprofeno, indEMS));
            criarOuAtualizarEstoque(medAlivium, farmaciaCentro, 25, "LOTE_ALIV001", LocalDate.of(2026,8,20), new BigDecimal("8.00"));

            System.out.println("Medicamentos e estoques iniciais carregados.");
        }

        System.out.println("DataLoader finalizado.");
    }

    private PrincipioAtivo criarOuCarregarPrincipioAtivo(String nome, Integer estoqueMinimo) {
        return principioAtivoRepository.findByNomeIgnoreCase(nome).map(pa -> {
            if (estoqueMinimo != null && (pa.getEstoqueMinimo() == null || !pa.getEstoqueMinimo().equals(estoqueMinimo))) {
                pa.setEstoqueMinimo(estoqueMinimo);
                return principioAtivoRepository.save(pa);
            }
            return pa;
        }).orElseGet(() -> {
            PrincipioAtivo novoPa = new PrincipioAtivo(nome);
            if (estoqueMinimo != null) novoPa.setEstoqueMinimo(estoqueMinimo);
            return principioAtivoRepository.save(novoPa);
        });
    }

    private Industria criarOuCarregarIndustria(String nome, String cnpj) {
        String cnpjNormalizado = StringUtils.hasText(cnpj) ? cnpj.replaceAll("[^0-9]", "") : null;
        if (StringUtils.hasText(cnpjNormalizado)) {
            return industriaRepository.findByCnpj(cnpjNormalizado).orElseGet(() -> industriaRepository.save(new Industria(null, nome, cnpjNormalizado)));
        }
        return industriaRepository.findByNomeIgnoreCase(nome).orElseGet(() -> industriaRepository.save(new Industria(null, nome, null)));
    }

    private Farmacia criarOuCarregarFarmacia(String nome, String cnpj, String endereco, String cidade, String uf, String telefone) {
        String cnpjNormalizado = StringUtils.hasText(cnpj) ? cnpj.replaceAll("[^0-9]", "") : null;
        return farmaciaRepository.findByNomeIgnoreCase(nome).orElseGet(()-> farmaciaRepository.save(new Farmacia(null, nome, endereco, cidade, uf, cnpjNormalizado, telefone)));
    }

    private void criarOuAtualizarEstoque(Medicamento medicamento, Farmacia farmacia, int quantidade, String lote, LocalDate validadeLote, BigDecimal precoCusto) {
        if (medicamento == null || farmacia == null) {
            System.err.println("Não foi possível criar/atualizar estoque: Medicamento ou Farmácia nulos.");
            return;
        }

        Estoque estoque = StringUtils.hasText(lote) ?
                estoqueRepository.findByMedicamentoAndFarmaciaAndLote(medicamento, farmacia, lote).orElse(null) :
                estoqueRepository.findByMedicamentoAndFarmacia(medicamento, farmacia).stream()
                        .filter(e -> !StringUtils.hasText(e.getLote()))
                        .findFirst()
                        .orElse(null);

        if (estoque == null) {
            estoque = new Estoque(null, medicamento, farmacia, quantidade, StringUtils.hasText(lote) ? lote : null, validadeLote, precoCusto, null); // <<<--- NULL ADICIONADO AQUI
        } else {
            estoque.setQuantidade(estoque.getQuantidade() + quantidade);
            if (validadeLote != null) estoque.setDataDeValidadeDoLote(validadeLote);
            if (precoCusto != null) estoque.setPrecoDeCustoDoLote(precoCusto);
        }
        estoqueRepository.save(estoque);
    }
}
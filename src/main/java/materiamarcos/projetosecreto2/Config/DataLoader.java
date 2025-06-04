package materiamarcos.projetosecreto2.Config;

import materiamarcos.projetosecreto2.Model.*;
import materiamarcos.projetosecreto2.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;
    @Autowired
    private IndustriaRepository industriaRepository;
    @Autowired
    private FarmaciaRepository farmaciaRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;

    @Override
    @Transactional // É uma boa prática para garantir que tudo seja salvo ou nada seja
    public void run(String... args) throws Exception {
        System.out.println("Iniciando DataLoader: Verificando e carregando dados iniciais...");

        // Criar Usuário de Teste/Admin se não existir
        if (usuarioRepository.count() == 0) {
            User adminUser = new User("admin", "admin@barateira.com", passwordEncoder.encode("admin123"));
            usuarioRepository.save(adminUser);
            System.out.println("Usuário 'admin' criado.");
        }

        // Criar Princípios Ativos
        PrincipioAtivo paParacetamol = null;
        PrincipioAtivo paDipirona = null;
        PrincipioAtivo paIbuprofeno = null;

        if (principioAtivoRepository.count() == 0) {
            paParacetamol = principioAtivoRepository.save(new PrincipioAtivo("Paracetamol"));
            paDipirona = principioAtivoRepository.save(new PrincipioAtivo("Dipirona Sódica"));
            paIbuprofeno = principioAtivoRepository.save(new PrincipioAtivo("Ibuprofeno"));
            System.out.println("Princípios Ativos de exemplo carregados.");
        } else {
            // Tenta carregar existentes se o count não for zero (para uso posterior)
            paParacetamol = principioAtivoRepository.findByNomeIgnoreCase("Paracetamol").orElse(null);
            paDipirona = principioAtivoRepository.findByNomeIgnoreCase("Dipirona Sódica").orElse(null);
            paIbuprofeno = principioAtivoRepository.findByNomeIgnoreCase("Ibuprofeno").orElse(null);
        }

        // Criar Indústrias
        Industria indMedley = null;
        Industria indNeoQuimica = null;
        if (industriaRepository.count() == 0) {
            indMedley = industriaRepository.save(new Industria("Medley Farmacêutica", "11111111000111"));
            indNeoQuimica = industriaRepository.save(new Industria("Neo Química", "22222222000122"));
            System.out.println("Indústrias de exemplo carregadas.");
        } else {
            indMedley = industriaRepository.findByNomeIgnoreCase("Medley Farmacêutica").orElse(null);
            indNeoQuimica = industriaRepository.findByNomeIgnoreCase("Neo Química").orElse(null);
        }

        // Criar Farmácias (Filiais)
        Farmacia farmaciaCentro = null;
        Farmacia farmaciaBairro = null;
        if (farmaciaRepository.count() == 0) {
            farmaciaCentro = farmaciaRepository.save(new Farmacia(null, "Barateira Filial Centro", "Rua Principal, 123", "Cidade Central", "SP", "33333333000133", "1133334444"));
            farmaciaBairro = farmaciaRepository.save(new Farmacia(null, "Barateira Filial Bairro Feliz", "Av. Secundária, 456", "Cidade Central", "SP", "44444444000144", "1155556666"));
            System.out.println("Farmácias de exemplo carregadas.");
        } else {
            farmaciaCentro = farmaciaRepository.findByNomeIgnoreCase("Barateira Filial Centro").orElse(null);
            farmaciaBairro = farmaciaRepository.findByNomeIgnoreCase("Barateira Filial Bairro Feliz").orElse(null);
        }

        // Criar Medicamentos (apenas se as dependências existirem e medicamentos estiverem vazios)
        if (medicamentoRepository.count() == 0 && paParacetamol != null && indNeoQuimica != null && paDipirona != null && indMedley != null) {
            Medicamento med1 = new Medicamento(null, "Tylenol 750mg", "Analgésico e antitérmico", "7890001001",
                    LocalDate.of(2026, 12, 31), new BigDecimal("5.50"), new BigDecimal("12.80"),
                    false, null, paParacetamol, indNeoQuimica);

            Medicamento med2 = new Medicamento(null, "Dorflex", "Relaxante muscular e analgésico", "7890001002",
                    LocalDate.of(2027, 10, 15), new BigDecimal("3.20"), new BigDecimal("7.50"),
                    true, new BigDecimal("6.99"), paDipirona, indMedley);

            Medicamento med3 = new Medicamento(null, "Alivium 400mg", "Anti-inflamatório", "7890001003",
                    LocalDate.of(2026, 8, 20), new BigDecimal("8.00"), new BigDecimal("15.00"),
                    false, null, paIbuprofeno, indMedley);

            medicamentoRepository.saveAll(Arrays.asList(med1, med2, med3));
            System.out.println("Medicamentos de exemplo carregados.");

            // Criar Estoque Inicial (apenas se farmácias e medicamentos existirem)
            if (estoqueRepository.count() == 0 && farmaciaCentro != null && farmaciaBairro != null) {
                estoqueRepository.save(new Estoque(null, med1, farmaciaCentro, 100, "LOTE001", LocalDate.of(2026,12,31), new BigDecimal("5.50"), null));
                estoqueRepository.save(new Estoque(null, med2, farmaciaCentro, 50, "LOTE002", LocalDate.of(2027,10,15), new BigDecimal("3.20"), null));
                estoqueRepository.save(new Estoque(null, med1, farmaciaBairro, 75, "LOTE003", LocalDate.of(2026,12,31), new BigDecimal("5.50"), null));
                estoqueRepository.save(new Estoque(null, med3, farmaciaCentro, 60, "LOTE004", LocalDate.of(2026,8,20), new BigDecimal("8.00"), null));
                System.out.println("Registros de estoque de exemplo carregados.");
            }

        } else if (medicamentoRepository.count() > 0) {
            System.out.println("Medicamentos já existentes no banco. Estoque inicial não será recriado automaticamente se já existir.");

        }


        System.out.println("DataLoader finalizado.");
    }
}
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
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Iniciando DataLoader: Verificando e carregando dados iniciais...");

        // Criar Usuário
        if (usuarioRepository.count() == 0) {
            User adminUser = new User("admin", "admin@barateira.com", passwordEncoder.encode("admin123"));
            usuarioRepository.save(adminUser);
            System.out.println("Usuário 'admin' criado.");
        }

        // Criar Princípios Ativos e Definir Estoque
        PrincipioAtivo paParacetamol = principioAtivoRepository.findByNomeIgnoreCase("Paracetamol").orElse(null);
        if (paParacetamol == null) {
            paParacetamol = new PrincipioAtivo("Paracetamol");
            paParacetamol.setEstoqueMinimo(15);
            paParacetamol = principioAtivoRepository.save(paParacetamol);
            System.out.println("Princípio Ativo 'Paracetamol' criado com estoque mínimo de 15.");
        } else {

            if (paParacetamol.getEstoqueMinimo() == null || paParacetamol.getEstoqueMinimo() != 15) {
                paParacetamol.setEstoqueMinimo(15);
                paParacetamol = principioAtivoRepository.save(paParacetamol);
                System.out.println("Estoque mínimo para 'Paracetamol' atualizado para 15.");
            }
        }

        PrincipioAtivo paDipirona = principioAtivoRepository.findByNomeIgnoreCase("Dipirona Sódica")
                .orElseGet(() -> principioAtivoRepository.save(new PrincipioAtivo("Dipirona Sódica")));

        PrincipioAtivo paIbuprofeno = principioAtivoRepository.findByNomeIgnoreCase("Ibuprofeno")
                .orElseGet(() -> {
                    PrincipioAtivo pa = new PrincipioAtivo("Ibuprofeno");
                    pa.setEstoqueMinimo(20);
                    System.out.println("Princípio Ativo 'Ibuprofeno' criado com estoque mínimo de 20.");
                    return principioAtivoRepository.save(pa);
                });


        // Criar Indústrias
        Industria indMedley = industriaRepository.findByNomeIgnoreCase("Medley Farmacêutica")
                .orElseGet(() -> industriaRepository.save(new Industria("Medley Farmacêutica", "11111111000111")));
        Industria indNeoQuimica = industriaRepository.findByNomeIgnoreCase("Neo Química")
                .orElseGet(() -> industriaRepository.save(new Industria("Neo Química", "22222222000122")));

        if (industriaRepository.count() <=2 && (indMedley.getId() == null || indNeoQuimica.getId() == null)) { // Se acabou de criar
            System.out.println("Indústrias de exemplo carregadas/verificadas.");
        }


        // Criar Farmácias (Filiais)
        Farmacia farmaciaCentro = farmaciaRepository.findByNomeIgnoreCase("Barateira Filial Centro")
                .orElseGet(() -> farmaciaRepository.save(new Farmacia(null, "Barateira Filial Centro", "Rua Principal, 123", "Cidade Central", "SP", "33333333000133", "1133334444")));
        Farmacia farmaciaBairro = farmaciaRepository.findByNomeIgnoreCase("Barateira Filial Bairro Feliz")
                .orElseGet(() -> farmaciaRepository.save(new Farmacia(null, "Barateira Filial Bairro Feliz", "Av. Secundária, 456", "Cidade Central", "SP", "44444444000144", "1155556666")));

        if (farmaciaRepository.count() <= 2 && (farmaciaCentro.getId() == null || farmaciaBairro.getId() == null )) { // Se acabou de criar
            System.out.println("Farmácias de exemplo carregadas/verificadas.");
        }


        // Criar Medicamentos
        Medicamento medTylenol = medicamentoRepository.findByCodigoDeBarras("7890001001").orElse(null);
        if (medTylenol == null && paParacetamol != null && indNeoQuimica != null) {
            medTylenol = new Medicamento(null, "Tylenol 750mg", "Analgésico e antitérmico", "7890001001",
                    LocalDate.of(2026, 12, 31), new BigDecimal("5.50"), new BigDecimal("12.80"),
                    false, null, paParacetamol, indNeoQuimica);
            medicamentoRepository.save(medTylenol);
            System.out.println("Medicamento Tylenol 750mg criado.");
        }

        Medicamento medDorflex = medicamentoRepository.findByCodigoDeBarras("7890001002").orElse(null);
        if (medDorflex == null && paDipirona != null && indMedley != null) {
            medDorflex = new Medicamento(null, "Dorflex", "Relaxante muscular e analgésico", "7890001002",
                    LocalDate.of(2027, 10, 15), new BigDecimal("3.20"), new BigDecimal("7.50"),
                    false, null, paDipirona, indMedley); // Inicialmente sem promoção
            medicamentoRepository.save(medDorflex);
            System.out.println("Medicamento Dorflex criado.");
        }


        // Criar Estoque Inicial para disparar o Alerta para Paracetamol
        // Vamos adicionar um estoque total de Paracetamol que seja <= 15.
        if (estoqueRepository.count() == 0 && medTylenol != null && farmaciaCentro != null && farmaciaBairro != null) {
            // Tylenol (Paracetamol) na Filial Centro - 5 unidades
            estoqueRepository.save(new Estoque(null, medTylenol, farmaciaCentro, 5, "LOTE_TYL001", LocalDate.of(2026,12,31), new BigDecimal("5.50"), null));

            // Tylenol (Paracetamol) na Filial Bairro - 5 unidades
            estoqueRepository.save(new Estoque(null, medTylenol, farmaciaBairro, 5, "LOTE_TYL002", LocalDate.of(2026,12,31), new BigDecimal("5.50"), null));
            // Total de Paracetamol (Tylenol) no sistema = 5 + 5 = 10, que é <= 15 (estoque mínimo)

            // Adicionar estoque para Dorflex também, para ter outros dados
            if (medDorflex != null) {
                estoqueRepository.save(new Estoque(null, medDorflex, farmaciaCentro, 50, "LOTE_DORF001", LocalDate.of(2027,10,15), new BigDecimal("3.20"), null));
            }
            System.out.println("Registros de estoque de exemplo carregados para teste de alerta.");
        }

        System.out.println("DataLoader finalizado.");
    }
}
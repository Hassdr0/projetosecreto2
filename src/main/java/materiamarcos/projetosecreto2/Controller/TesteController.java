package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.Repository.TesteRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/teste")
public class TesteController {

    @Autowired
    private TesteRepository testeRepository;

    @PostMapping
    public Teste criar(@RequestBody Teste teste) {
        return testeRepository.save(teste);
    }

    @GetMapping
    public List<Teste> listar() {
        return testeRepository.findAll();
    }
}
package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.CotacaoRequestDTO;
import materiamarcos.projetosecreto2.DTOs.CotacaoResponseDTO;
import materiamarcos.projetosecreto2.Service.CotacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotacoes")
public class CotacaoController {

    @Autowired
    private CotacaoService cotacaoService;

    @PostMapping
    public ResponseEntity<CotacaoResponseDTO> registrarCotacao(@Valid @RequestBody CotacaoRequestDTO requestDTO) {
        CotacaoResponseDTO cotacaoSalva = cotacaoService.registrarCotacao(requestDTO);
        return new ResponseEntity<>(cotacaoSalva, HttpStatus.CREATED);
    }

    @GetMapping("/medicamento/{medicamentoId}")
    public ResponseEntity<List<CotacaoResponseDTO>> buscarCotacoesPorMedicamento(@PathVariable Long medicamentoId) {
        List<CotacaoResponseDTO> cotacoes = cotacaoService.buscarCotacoesAtivasPorMedicamento(medicamentoId);
        return ResponseEntity.ok(cotacoes);
    }
}
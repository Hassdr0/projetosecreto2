package materiamarcos.projetosecreto2.Controller;

import materiamarcos.projetosecreto2.DTOs.CompraRequestDTO;
import materiamarcos.projetosecreto2.DTOs.ConfirmarRecebimentoCompraDTO;
import materiamarcos.projetosecreto2.Model.Compra;
import materiamarcos.projetosecreto2.Service.CompraService;
import materiamarcos.projetosecreto2.exception.EstoqueInsuficienteException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    // Endpoint para criar uma nova compra baseada na melhor cotação
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarCompra(@Valid @RequestBody CompraRequestDTO requestDTO) {
        try {
            Compra novaCompra = compraService.criarCompraDeMelhorCotacao(requestDTO);
            //ter um CompraResponseDTO aqui, mas por simplicidade retornamos a entidade
            return new ResponseEntity<>(novaCompra, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Endpoint para confirmar o recebimento de uma compra (e dar entrada no estoque)
    @PostMapping("/{id}/receber")
    public ResponseEntity<?> receberCompra(@PathVariable Long id, @Valid @RequestBody ConfirmarRecebimentoCompraDTO requestDTO) {
        try {
            Compra compraRecebida = compraService.confirmarRecebimentoCompra(id, requestDTO);
            return ResponseEntity.ok(compraRecebida);
        } catch (RuntimeException | EstoqueInsuficienteException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
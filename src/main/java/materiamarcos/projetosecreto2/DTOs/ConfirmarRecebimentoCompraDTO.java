package materiamarcos.projetosecreto2.DTOs;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmarRecebimentoCompraDTO {
    @NotBlank private String numeroNotaFiscal;
    //adicionar uma lista de lotes e validades aqui se eles não vieram no pedido de compra original
}
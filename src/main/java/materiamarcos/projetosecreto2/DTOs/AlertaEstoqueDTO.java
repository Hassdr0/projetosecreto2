package materiamarcos.projetosecreto2.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um alerta de estoque baixo para um princípio ativo.")
public class AlertaEstoqueDTO {

    @Schema(description = "ID do princípio ativo com estoque baixo.", example = "1")
    private Long principioAtivoId;

    @Schema(description = "Nome do princípio ativo.", example = "Paracetamol")
    private String nomePrincipioAtivo;

    @Schema(description = "Quantidade total atual em estoque de todos os medicamentos com este princípio ativo.", example = "10")
    private Integer quantidadeAtualTotal;

    @Schema(description = "Quantidade mínima definida para este princípio ativo.", example = "15")
    private Integer estoqueMinimoDefinido;

    @Schema(description = "Mensagem do alerta.", example = "Estoque baixo para o princípio ativo.")
    private String mensagem;
}

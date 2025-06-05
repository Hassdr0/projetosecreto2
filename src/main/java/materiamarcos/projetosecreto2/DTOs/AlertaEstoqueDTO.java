package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaEstoqueDTO {
    private Long principioAtivoId;
    private String nomePrincipioAtivo;
    private Integer quantidadeAtualTotal;
    private Integer estoqueMinimoDefinido;
    private String mensagem; // Ex: "Estoque baixo", "Estoque crítico"
}
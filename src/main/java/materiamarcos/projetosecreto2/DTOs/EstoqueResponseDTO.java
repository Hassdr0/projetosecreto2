package materiamarcos.projetosecreto2.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {
    private Long id;
    private MedicamentoResponseDTO medicamento;
    private FarmaciaResponseDTO farmacia;
    private Integer quantidade;
    private String lote;
    private LocalDate dataDeValidadeDoLote;
    private BigDecimal precoDeCustoDoLote;
    private LocalDateTime dataUltimaAtualizacao;
}
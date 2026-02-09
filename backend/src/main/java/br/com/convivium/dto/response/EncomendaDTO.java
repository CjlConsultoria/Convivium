package br.com.convivium.dto.response;

import br.com.convivium.entity.enums.StatusEncomenda;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EncomendaDTO {
    private Long id;
    private String codigoRetirada;
    private StatusEncomenda status;
    private String descricao;
    private LocalDateTime dataRecebimento;
    private LocalDateTime dataRetirada;
    private Long moradorId;
    private String moradorNome;
    private Long empresaId;
    private String empresaNome;
    private Long registradoPorId;
    private String registradoPorNome;
    private Long retiradaPorId;
    private String retiradaPorNome;
}

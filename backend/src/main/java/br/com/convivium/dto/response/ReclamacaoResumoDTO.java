package br.com.convivium.dto.response;

import br.com.convivium.entity.enums.StatusReclamacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamacaoResumoDTO {
    private String unidade;
    private String tipo;
    private StatusReclamacao status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataResolucao;
    private String localReclamado;

    // construtores, getters, setters
}


package br.com.convivium.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LicencaDTO {
    private Long id;
    private Long empresaId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativa;
    private String tipo;
    private Integer limiteUsuarios;
}





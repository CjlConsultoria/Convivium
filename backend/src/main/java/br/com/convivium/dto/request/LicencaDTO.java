package br.com.convivium.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LicencaDTO {
    private Long id; // para edição
    private Long empresaId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativa;
    private String tipo;
    private Integer limiteUsuarios;
}


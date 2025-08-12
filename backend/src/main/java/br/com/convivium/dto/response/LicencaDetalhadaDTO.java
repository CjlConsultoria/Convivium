package br.com.convivium.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LicencaDetalhadaDTO {
    private Long id;
    private Long empresaId;
    private String empresaNome;
    private String empresaCnpj;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativa;
    private String tipo;
    private Integer limiteUsuarios;

    private Long responsavelId;
    private String responsavelNome;
    private String responsavelCpf;
    private String perfil;

    private Boolean validadeExpirada;
    private Long diasRestantes;
}



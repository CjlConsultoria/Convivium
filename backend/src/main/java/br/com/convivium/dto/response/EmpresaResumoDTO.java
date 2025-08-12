package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class EmpresaResumoDTO {
    private Long id;
    private String nome;
    private String cnpj;
    private String codigoPublico;
}


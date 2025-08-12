package br.com.convivium.dto.request;

import lombok.Data;

@Data
public class VerificarCpfRequest {
    private String cpf;
    private Long idCondominio;
}


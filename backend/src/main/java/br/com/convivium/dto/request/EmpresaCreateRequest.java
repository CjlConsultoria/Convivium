package br.com.convivium.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaCreateRequest {
    private String name;
    private String cnpj;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
}


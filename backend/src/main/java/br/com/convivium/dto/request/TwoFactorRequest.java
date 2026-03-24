package br.com.convivium.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TwoFactorRequest {
    @NotBlank(message = "Código 2FA é obrigatório")
    private String twoFactorCode;
    
    @NotBlank(message = "Token de encomenda é obrigatório")
    private String encomendaToken;
}

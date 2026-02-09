package br.com.convivium.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EncomendaRetiradaRequest {
    @NotBlank(message = "Código de retirada é obrigatório")
    private String codigoRetirada;
}

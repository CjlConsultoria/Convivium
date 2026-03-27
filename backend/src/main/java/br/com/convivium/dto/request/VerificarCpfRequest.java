package br.com.convivium.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class VerificarCpfRequest {
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;
    
    @NotNull(message = "ID do condomínio é obrigatório")
    private Long idCondominio;
}
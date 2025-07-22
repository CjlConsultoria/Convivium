package br.com.convivium.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResetarSenhaRequest {
    private String token;
    private String novaSenha;
    private String cpf;
}



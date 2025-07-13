package br.com.convivium.dto.request;

import lombok.Data;

@Data
public class AtivacaoContaRequest {
    private String senha;

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}


package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class VerificarCpfResponse {
    private String status; // "nao_encontrado", "ativo", "pendente_ativacao"
    private Long idUsuario;
    private String nome;
    // CPF removido para não expor dados sensíveis na resposta

    public VerificarCpfResponse(String status) {
        this.status = status;
    }

    public VerificarCpfResponse(String status, Long idUsuario, String nome) {
        this.status = status;
        this.idUsuario = idUsuario;
        this.nome = nome;
    }
}
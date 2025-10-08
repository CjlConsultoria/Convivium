package br.com.convivium.dto.request;

public class ChatRequest {
    private String message;
    private Long condominioId;   // id do condomínio
    private Long usuarioId;      // id do usuário logado

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}


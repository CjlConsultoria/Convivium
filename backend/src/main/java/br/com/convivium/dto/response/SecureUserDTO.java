package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class SecureUserDTO {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private Long empresaId;
    private String empresaNome;
    
    // Construtor que remove dados sensíveis
    public SecureUserDTO(Long id, String nome, String email, String role, Long empresaId, String empresaNome) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
    }
}
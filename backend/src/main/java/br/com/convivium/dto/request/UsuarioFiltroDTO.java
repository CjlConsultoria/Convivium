package br.com.convivium.dto.request;

import lombok.Data;

@Data
public class UsuarioFiltroDTO {
    private String nome;
    private String cpf;
    private Long empresaId; // Added empresaId field for multi-tenant filtering
}
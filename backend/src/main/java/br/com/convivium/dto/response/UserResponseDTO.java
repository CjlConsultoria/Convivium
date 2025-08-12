package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String cpf;
    private String telefone;
    private boolean ativo;
    private String sobrenome;
    private String genero;
    private String cep;
    private String logradouro;
    private String cidade;
    private String estado;
    private String bairro;
    private String numero;
    private String complemento;
    private boolean alerta;
    private String bloco;
    private String apartamento;
    private String vagaCarro;
    private String vagaMoto;
    private String role;
    private String tipo;
    private String empresa;
}

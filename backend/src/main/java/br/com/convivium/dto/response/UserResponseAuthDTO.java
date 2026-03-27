package br.com.convivium.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseAuthDTO {
    private Long id;
    private String username;
    private String email;
    private boolean ativo;
    private String sobrenome;
    private boolean alerta;
    private String bloco;
    private String apartamento;
    private String role;
    private String tipo;
    private EmpresaResumoDTO empresa;
    
    // Dados sensíveis - apenas para o próprio usuário autenticado
    private UserSensitiveDataDTO dadosCompletos;
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserSensitiveDataDTO {
        private String cpf;
        private String telefone;
        private String genero;
        private String cep;
        private String logradouro;
        private String cidade;
        private String estado;
        private String bairro;
        private String numero;
        private String complemento;
        private String vagaCarro;
        private String vagaMoto;
    }
}
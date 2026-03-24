package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class SecureEmpresaDTO {
    private Long id;
    private String name;
    private String codigoPublico;
    private String cidade;
    private String estado;
    
    // Remove dados sensíveis como CNPJ completo, endereço completo
    public SecureEmpresaDTO(Long id, String name, String codigoPublico, String cidade, String estado) {
        this.id = id;
        this.name = name;
        this.codigoPublico = codigoPublico;
        this.cidade = cidade;
        this.estado = estado;
    }
}
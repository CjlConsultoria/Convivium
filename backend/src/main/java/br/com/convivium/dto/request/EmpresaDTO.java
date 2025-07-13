package br.com.convivium.dto.request;

import br.com.convivium.entity.Empresa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDTO {

    private Long id;
    private String name;
    private String codigoPublico;
    private String cidade;
    private String estado;

    public EmpresaDTO(Empresa empresa) {
        this.id = empresa.getId();
        this.name = empresa.getName();
        this.codigoPublico = empresa.getCodigoPublico();
        this.cidade = empresa.getCidade();
        this.estado = empresa.getEstado();
    }
}


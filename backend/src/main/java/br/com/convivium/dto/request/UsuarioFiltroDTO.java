package br.com.convivium.dto.request;

import lombok.Data;
import javax.validation.constraints.Size;

@Data
public class UsuarioFiltroDTO {
    @Size(min = 2, message = "Nome deve ter pelo menos 2 caracteres")
    private String nome;
    
    // CPF removido dos filtros para evitar exposição indevida
    // Busca por CPF deve ser feita apenas por administradores em endpoints específicos
}
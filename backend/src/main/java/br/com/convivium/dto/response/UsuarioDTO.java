package br.com.convivium.dto.response;

import br.com.convivium.entity.User;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String bloco;
    private String apartamento;
    private String tipoPerfil;

    public UsuarioDTO() {
    }

    public UsuarioDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bloco = user.getBloco();
        this.apartamento = user.getApartamento();
        this.tipoPerfil = user.getRole() != null ? user.getRole().getName() : null;
    }
}



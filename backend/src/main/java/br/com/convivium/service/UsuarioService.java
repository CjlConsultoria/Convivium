package br.com.convivium.service;

import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.dto.response.UsuarioDTO;
import br.com.convivium.entity.User;
import br.com.convivium.entity.specification.UsuarioSpecification;
import br.com.convivium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UserRepository userRepository;

    public Page<UsuarioDTO> filtrarUsuarios(UsuarioFiltroDTO filtro, Pageable pageable) {
        // Apply specification with multi-tenant filtering
        Page<User> usuarios = userRepository.findAll(
            UsuarioSpecification.filtrarPorNomeECpf(filtro), 
            pageable
        );
        return usuarios.map(UsuarioDTO::new);
    }

    public Long getEmpresaIdFromAuthentication(Authentication authentication) {
        // Extract empresaId from authenticated user
        // This assumes the User entity has an empresa relationship
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getEmpresa() == null) {
            throw new RuntimeException("User has no associated empresa");
        }
        
        return user.getEmpresa().getId();
    }
}
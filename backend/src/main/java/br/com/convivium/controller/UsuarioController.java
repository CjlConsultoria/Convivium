package br.com.convivium.controller;

import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.dto.response.UsuarioDTO;
import br.com.convivium.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SINDICO') or hasRole('PORTEIRO') or hasRole('MORADOR')") // Added authentication requirement
    public ResponseEntity<Page<UsuarioDTO>> filtrarUsuarios(
            @RequestBody UsuarioFiltroDTO filtro,
            Pageable pageable,
            Authentication authentication) {
        
        // Get empresaId from authenticated user to enforce multi-tenant isolation
        Long empresaId = usuarioService.getEmpresaIdFromAuthentication(authentication);
        filtro.setEmpresaId(empresaId); // Force empresaId from authenticated user
        
        Page<UsuarioDTO> usuarios = usuarioService.filtrarUsuarios(filtro, pageable);
        return ResponseEntity.ok(usuarios);
    }
}
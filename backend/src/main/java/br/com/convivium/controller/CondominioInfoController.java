package br.com.convivium.controller;

import br.com.convivium.dto.response.CondominioInfoDTO;
import br.com.convivium.entity.User;
import br.com.convivium.service.CondominioInfoService;
import br.com.convivium.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/condominio-info")
public class CondominioInfoController {

    private final CondominioInfoService condominioInfoService;
    private final AuthenticationService authenticationService;

    public CondominioInfoController(CondominioInfoService condominioInfoService,
                                    AuthenticationService authenticationService) {
        this.condominioInfoService = condominioInfoService;
        this.authenticationService = authenticationService;
    }

    private boolean podeAcessarEmpresa(Long empresaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        String cpf = (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails)
                ? ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername()
                : null;
        if (cpf == null) return false;
        User user = authenticationService.getUserDetails(cpf);
        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        if (user.getEmpresa() == null) return isAdmin;
        return user.getEmpresa().getId().equals(empresaId) || isAdmin;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<CondominioInfoDTO> buscarPorEmpresa(@PathVariable Long empresaId) {
        if (!podeAcessarEmpresa(empresaId)) {
            return ResponseEntity.status(403).build();
        }
        CondominioInfoDTO dto = condominioInfoService.buscarPorEmpresaId(empresaId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/empresa/{empresaId}")
    public ResponseEntity<CondominioInfoDTO> atualizar(
            @PathVariable Long empresaId,
            @RequestBody CondominioInfoDTO dto) {
        if (!podeAcessarEmpresa(empresaId)) {
            return ResponseEntity.status(403).build();
        }
        CondominioInfoDTO atualizado = condominioInfoService.criarOuAtualizar(empresaId, dto);
        return ResponseEntity.ok(atualizado);
    }
}

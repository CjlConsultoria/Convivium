package br.com.convivium.controller;

import br.com.convivium.dto.request.EncomendaRegistroRequest;
import br.com.convivium.dto.request.EncomendaRetiradaRequest;
import br.com.convivium.dto.request.TwoFactorRequest;
import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.entity.User;
import br.com.convivium.entity.enums.RoleType;
import br.com.convivium.service.AuthenticationService;
import br.com.convivium.service.EncomendaService;
import br.com.convivium.service.TwoFactorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;
    private final AuthenticationService authenticationService;
    private final TwoFactorService twoFactorService;

    public EncomendaController(EncomendaService encomendaService, AuthenticationService authenticationService, TwoFactorService twoFactorService) {
        this.encomendaService = encomendaService;
        this.authenticationService = authenticationService;
        this.twoFactorService = twoFactorService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        String cpf = (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails)
                ? ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername()
                : null;
        if (cpf == null) return null;
        return authenticationService.getUserDetails(cpf);
    }

    private boolean podeRegistrarOuValidarEncomenda(User user) {
        if (user == null) return false;
        String r = user.getRole() != null ? user.getRole().getName() : null;
        return RoleType.ADMIN.getNome().equals(r) || RoleType.ADMINISTRATIVO.getNome().equals(r)
                || RoleType.SINDICO.getNome().equals(r) || RoleType.SUB_SINDICO.getNome().equals(r)
                || RoleType.PORTARIA.getNome().equals(r) || RoleType.SEGURANCA.getNome().equals(r)
                || RoleType.ZELADOR.getNome().equals(r);
    }

    private boolean podeVerTodasEncomendasEmpresa(User user) {
        return podeRegistrarOuValidarEncomenda(user)
                || (user.getRole() != null && RoleType.CONSELHEIRO.getNome().equals(user.getRole().getName()));
    }

    @PostMapping("/request-2fa")
    public ResponseEntity<?> requestTwoFactor(@RequestParam String action) {
        User current = getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        if (!podeRegistrarOuValidarEncomenda(current)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para operações de encomenda.");
        }
        
        String code = twoFactorService.generateTwoFactorCode(current, action);
        String token = twoFactorService.generateEncomendaToken(current, action);
        
        return ResponseEntity.ok(Map.of(
            "message", "Código 2FA enviado para seu email",
            "token", token
        ));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody EncomendaRegistroRequest request,
                                     @RequestHeader(value = "X-2FA-Code", required = false) String twoFactorCode,
                                     @RequestHeader(value = "X-Encomenda-Token", required = false) String encomendaToken) {
        User current = getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!podeRegistrarOuValidarEncomenda(current)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para registrar encomenda.");
        }
        if (current.getEmpresa() != null && !current.getEmpresa().getId().equals(request.getEmpresaId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Condomínio não autorizado.");
        }
        
        // Verificar autenticação 2FA
        if (Boolean.TRUE.equals(current.getTwoFactorEnabled())) {
            if (twoFactorCode == null || encomendaToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autenticação de dois fatores obrigatória. Use /request-2fa primeiro.");
            }
            
            if (!twoFactorService.validateTwoFactorCode(current, twoFactorCode) || 
                !twoFactorService.validateEncomendaToken(encomendaToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA inválido ou expirado.");
            }
        }
        
        EncomendaDTO dto = encomendaService.toDto(encomendaService.registrar(request, current.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/retirada")
    public ResponseEntity<?> marcarRetirada(@Valid @RequestBody EncomendaRetiradaRequest request,
                                            @RequestParam Long empresaId,
                                            @RequestHeader(value = "X-2FA-Code", required = false) String twoFactorCode,
                                            @RequestHeader(value = "X-Encomenda-Token", required = false) String encomendaToken) {
        User current = getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!podeRegistrarOuValidarEncomenda(current)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para validar retirada.");
        }
        if (current.getEmpresa() != null && !current.getEmpresa().getId().equals(empresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Condomínio não autorizado.");
        }
        
        // Verificar autenticação 2FA
        if (Boolean.TRUE.equals(current.getTwoFactorEnabled())) {
            if (twoFactorCode == null || encomendaToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autenticação de dois fatores obrigatória. Use /request-2fa primeiro.");
            }
            
            if (!twoFactorService.validateTwoFactorCode(current, twoFactorCode) || 
                !twoFactorService.validateEncomendaToken(encomendaToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA inválido ou expirado.");
            }
        }
        
        EncomendaDTO dto = encomendaService.toDto(encomendaService.marcarRetirada(request, empresaId, current.getId()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<EncomendaDTO>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long moradorId,
            Pageable pageable) {
        User current = getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long eid = empresaId != null ? empresaId : (current.getEmpresa() != null ? current.getEmpresa().getId() : null);
        if (eid == null && empresaId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (moradorId != null) {
            boolean proprioMorador = current.getId().equals(moradorId);
            boolean gestao = podeVerTodasEncomendasEmpresa(current);
            if (!proprioMorador && !gestao) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (current.getEmpresa() != null && !current.getEmpresa().getId().equals(eid)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(encomendaService.listarPorMorador(moradorId, eid, pageable));
        }

        if (!podeVerTodasEncomendasEmpresa(current)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (current.getEmpresa() != null && !current.getEmpresa().getId().equals(eid)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(encomendaService.listarPorEmpresa(eid, pageable));
    }

    @GetMapping("/codigo/{codigoRetirada}")
    public ResponseEntity<EncomendaDTO> buscarPorCodigo(
            @PathVariable String codigoRetirada,
            @RequestParam Long empresaId) {
        User current = getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!podeRegistrarOuValidarEncomenda(current)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (current.getEmpresa() != null && !current.getEmpresa().getId().equals(empresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<EncomendaDTO> opt = encomendaService.buscarPorCodigo(codigoRetirada, empresaId);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}

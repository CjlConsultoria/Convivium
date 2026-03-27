package br.com.convivium.controller;

import br.com.convivium.dto.UserMapper;
import br.com.convivium.dto.request.*;
import br.com.convivium.dto.response.AuthResponse;
import br.com.convivium.dto.response.UserResponseAuthDTO;
import br.com.convivium.dto.response.UserResponseDTO;
import br.com.convivium.dto.response.UsuarioDTO;
import br.com.convivium.entity.User;
import br.com.convivium.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager) {
    }

    @Operation(summary = "Login para obter token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticationService.generateToken(loginRequest.getCpf(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Registrar novo usuário")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Usuário criado com sucesso"));
    }

    @Operation(summary = "Esqueceu a senha")
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest emailCpf) {
        Map<String, Object> result = authenticationService.forgotPassword(emailCpf.getEmailCpf());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody ResetarSenhaRequest request) {
        authenticationService.resetarSenha(request.getToken(), request.getNovaSenha(), request.getCpf());
        return ResponseEntity.ok(Map.of("success", true, "message", "Senha redefinida com sucesso."));
    }

    @Operation(summary = "Obter detalhes do usuário autenticado")
    @GetMapping("/user")
    public ResponseEntity<UserResponseAuthDTO> getUserDetails() {
        String cpf = getCurrentUsername();

        if (cpf == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = authenticationService.getUserDetails(cpf);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PutMapping("/usuario/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @Valid @RequestBody RegisterRequest userUpdateRequest) {
        
        // Verificar se o usuário está tentando atualizar seus próprios dados
        String currentUserCpf = getCurrentUsername();
        User currentUser = authenticationService.getUserDetails(currentUserCpf);
        
        if (!currentUser.getId().toString().equals(id) && 
            !("ADMIN".equals(currentUser.getRole().getName()) || "ADMINISTRATIVO".equals(currentUser.getRole().getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Acesso negado para atualizar este usuário."));
        }
        
        authenticationService.updateUserData(id, userUpdateRequest);
        return ResponseEntity.ok(Map.of("success", true, "message", "Usuário atualizado com sucesso."));
    }

    @DeleteMapping("/usuario/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        authenticationService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Usuário deletado com sucesso."));
    }

    @GetMapping("/buscar-por-cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<UsuarioDTO> buscarPorCpf(@PathVariable String cpf) {
        // CPF deve ser validado e limpo antes da busca
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return ResponseEntity.badRequest().build();
        }
        
        return authenticationService.buscarPorCpf(cpfLimpo)
                .map(user -> ResponseEntity.ok(new UsuarioDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // aqui pega o CPF
        } else if (principal instanceof String) {
            return (String) principal;
        }

        return null;
    }
}
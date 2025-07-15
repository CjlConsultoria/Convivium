package br.com.convivium.controller;

import br.com.convivium.dto.request.*;
import br.com.convivium.dto.response.AuthResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    // Constructor for AuthenticationManager (optional, as AuthenticationService is already autowired)
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager) {
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authenticationService.generateToken(loginRequest.getCpf(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Register a new user", description = "Create a new user and save it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User  registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Usuário criado com sucesso"));    }

    @Operation(summary = "Register a new user", description = "Create a new user and save it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User  registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest emailCpf) {
        authenticationService.forgotPassword(emailCpf.getEmailCpf());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("success", true, "message", "Usuário criado com sucesso"));    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> redefinirSenha(@RequestBody ResetarSenhaRequest request) {
        boolean sucesso = authenticationService.resetarSenha(request.getToken(), request.getNovaSenha());
        if (sucesso) {
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido ou expirado.");
        }
    }

    @Operation(summary = "Get user details", description = "Retrieve the details of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User  details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid token")
    })
    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails() {
        String cpf = getCurrentUsername();

        if (cpf == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = authenticationService.getUserDetails(cpf); // busca usuário pelo CPF
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(user);
    }



    @PutMapping("/usuario/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody RegisterRequest userUpdateRequest) {

        try {
            authenticationService.updateUserData(id, userUpdateRequest);
            return ResponseEntity.ok("Usuário atualizado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar os dados do usuário.");
        }
    }

    @DeleteMapping("/usuario/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            authenticationService.deleteUserById(id);
            return ResponseEntity.ok("Usuário deletado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao deletar o usuário.");
        }
    }

    @GetMapping("/buscar-por-cpf/{cpf}")
    public ResponseEntity<UsuarioDTO> buscarPorCpf(@PathVariable String cpf) {
        return authenticationService.buscarPorCpf(cpf)
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

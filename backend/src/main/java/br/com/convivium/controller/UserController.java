package br.com.convivium.controller;

import br.com.convivium.dto.request.AtivacaoContaRequest;
import br.com.convivium.entity.Role;
import br.com.convivium.entity.Tipo;
import br.com.convivium.entity.User;
import br.com.convivium.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/")
public class UserController {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping("/list/{idEmpresa}")
    public ResponseEntity<Page<User>> listarPaginado(
            @PathVariable Long idEmpresa,
            @PageableDefault(page = 0, size = 10, sort = "username") Pageable pageable) {

        Page<User> page = userService.listAll(idEmpresa, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping("/list/tipo")
    public ResponseEntity<List<Tipo>> listarTipo() {
        return ResponseEntity.ok(userService.listTipo());
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping("/list/role")
    public ResponseEntity<List<Role>> listarPermissao() {
        return ResponseEntity.ok(userService.listarPermissao());
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> listarPermissao(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarUserId(id));
    }

    @PutMapping("/ativar-conta/{idUsuario}")
    public ResponseEntity<?> ativarConta(@PathVariable Long idUsuario, @RequestBody AtivacaoContaRequest request) {
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        userService.ativarConta(idUsuario, senhaCriptografada);
        return ResponseEntity.ok().build();
    }

}

package br.com.convivium.controller;

import br.com.convivium.dto.request.AtivacaoContaRequest;
import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.dto.response.UserResponseDTO;
import br.com.convivium.entity.Role;
import br.com.convivium.entity.Tipo;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @Operation(summary = "Lista usuários paginados", description = "Lista usuários de uma empresa com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição")
    })
    @GetMapping("/list/{idEmpresa}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<Page<UserResponseDTO>> listarPaginado(
            @PathVariable Long idEmpresa,
            @PageableDefault(page = 0, size = 10, sort = "username") Pageable pageable) {

        Page<UserResponseDTO> page = userService.listarUsuariosSemSenha(idEmpresa, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Lista tipos de usuário", description = "Retorna todos os tipos de usuário disponíveis")
    @GetMapping("/list/tipo")
    public ResponseEntity<List<Tipo>> listarTipo() {
        return ResponseEntity.ok(userService.listTipo());
    }

    @Operation(summary = "Lista roles/permissões", description = "Retorna todas as roles disponíveis")
    @GetMapping("/list/role")
    public ResponseEntity<List<Role>> listarPermissao() {
        return ResponseEntity.ok(userService.listarPermissao());
    }

    @Operation(summary = "Busca usuário por ID", description = "Retorna dados do usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarPorIdComDTO(id));
    }

    @PutMapping("/ativar-conta/{idUsuario}")
    public ResponseEntity<?> ativarConta(@PathVariable Long idUsuario, @RequestBody AtivacaoContaRequest request) {
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        userService.ativarConta(idUsuario, senhaCriptografada, request.getToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listar-simples/{idEmpresa}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<Page<UserResponseDTO>> listarSemSenha(
            @PathVariable Long idEmpresa,
            @PageableDefault(page = 0, size = 10, sort = "username") Pageable pageable) {
        Page<UserResponseDTO> page = userService.listarUsuariosSemSenha(idEmpresa, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<Page<UserResponseDTO>> filtrarUsuarios(
            @Valid @RequestBody UsuarioFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "username") Pageable pageable) {
        Page<UserResponseDTO> resultado = userService.listarComFiltro(filtro, pageable);
        return ResponseEntity.ok(resultado);
    }
}
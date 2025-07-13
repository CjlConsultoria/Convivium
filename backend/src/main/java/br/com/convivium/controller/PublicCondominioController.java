package br.com.convivium.controller;

import br.com.convivium.dto.request.EmpresaDTO;
import br.com.convivium.dto.request.VerificarCpfRequest;
import br.com.convivium.dto.response.VerificarCpfResponse;
import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.User;
import br.com.convivium.service.EmpresaService;
import br.com.convivium.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/condominio")
public class PublicCondominioController {

    private final UserService usuarioService;
    private final EmpresaService empresaService;

    public PublicCondominioController(UserService usuarioService, EmpresaService empresaService) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
    }

    @PostMapping("/verificar-cpf")
    public VerificarCpfResponse verificarCpf(@RequestBody VerificarCpfRequest request) {
        User usuario = usuarioService.buscarPorCpfECondominio(request.getCpf(), request.getIdCondominio());

        if (usuario == null) {
            return new VerificarCpfResponse("nao_encontrado");
        }

        if (usuario.getStatus().equals("ativo")) {
            return new VerificarCpfResponse("ativo");
        }

        return new VerificarCpfResponse("pendente_ativacao", usuario.getId(), usuario.getUsername());
    }

    @GetMapping("/codigo/{codigoPublico}")
    public ResponseEntity<EmpresaDTO> buscarPorCodigoPublico(@PathVariable String codigoPublico) {
        Empresa empresa = empresaService.buscarPorCodigoPublico(codigoPublico);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new EmpresaDTO(empresa));
    }

}


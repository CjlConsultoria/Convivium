package br.com.convivium.controller;

import br.com.convivium.dto.request.AcaoReclamacaoDTO;
import br.com.convivium.dto.request.ReclamacaoFiltroDTO;
import br.com.convivium.dto.request.SolucaoReclamacaoDTO;
import br.com.convivium.dto.response.ReclamacaoDTO;
import br.com.convivium.entity.AcaoReclamacao;
import br.com.convivium.entity.Reclamacao;
import br.com.convivium.service.ReclamacaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reclamacoes")
public class ReclamacaoController {

    private final ReclamacaoService reclamacaoService;

    public ReclamacaoController(ReclamacaoService reclamacaoService) {
        this.reclamacaoService = reclamacaoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> criarReclamacao(
            @RequestParam("tipo") String tipo,
            @RequestParam("detalhes") String detalhes,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("empresaId") Long empresaId,
            @RequestParam(value = "arquivos", required = false) MultipartFile[] arquivos
    ) {
        try {
            Reclamacao reclamacao = reclamacaoService.criarReclamacao(tipo, detalhes, usuarioId, empresaId, arquivos);
            ReclamacaoDTO dto = reclamacaoService.toDto(reclamacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<ReclamacaoDTO>> listar(@ModelAttribute
            ReclamacaoFiltroDTO filtro,
            Pageable pageable
    ) {
        Page<ReclamacaoDTO> resultado = reclamacaoService.listarComFiltro(filtro, pageable);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{id}/acoes")
    public ResponseEntity<AcaoReclamacao> adicionarAcao(
            @PathVariable Long id,
            @RequestBody AcaoReclamacaoDTO dto) {
        AcaoReclamacao acao = reclamacaoService.adicionarAcao(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(acao);
    }

    @PostMapping("/{id}/solucao")
    public ResponseEntity<Reclamacao> solucionarReclamacao(
            @PathVariable Long id,
            @RequestBody SolucaoReclamacaoDTO dto) {
        Reclamacao reclamacao = reclamacaoService.solucionarReclamacao(id, dto);
        return ResponseEntity.ok(reclamacao);
    }

    @GetMapping("/all")
    public List<AcaoReclamacao> listarTodas() {
        return reclamacaoService.listarTodas();
    }

    // Retorna ações por ID da reclamação
    @GetMapping("/{id}")
    public List<AcaoReclamacao> listarPorReclamacao(@PathVariable Long id) {
        return reclamacaoService.listarPorReclamacaoId(id);
    }

}


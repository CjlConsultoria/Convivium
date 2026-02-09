package br.com.convivium.controller;

import br.com.convivium.dto.response.RelatorioDTO;
import br.com.convivium.service.ReclamacaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final ReclamacaoService service;

    public RelatorioController(ReclamacaoService service) {
        this.service = service;
    }

    // Endpoint principal que retorna dados da tela de relatórios (filtrado por empresa quando informado)
    @GetMapping("/metricas")
    public ResponseEntity<RelatorioDTO> buscarMetricas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long empresaId) {

        RelatorioDTO dto = new RelatorioDTO();
        dto.setTotalReclamacoes(service.contarTotalReclamacoes(inicio, fim, empresaId));
        dto.setTotalPendentes(service.contarPendentes(inicio, fim, empresaId));
        dto.setTempoMedioResolucao(service.calcularTempoMedioResolucao(inicio, fim, empresaId));
        dto.setUnidadeMaisReclama(service.unidadeComMaisReclamacoes(inicio, fim, empresaId));
        dto.setUltimasReclamacoes(service.ultimas10ReclamacoesAbertas(empresaId));
        dto.setTopUnidadesQueMaisReclamam(service.topUnidadesQueMaisReclamam(inicio, fim, empresaId));
        dto.setTopUnidadesMaisReclamadas(service.topUnidadesMaisReclamadas(inicio, fim, empresaId));

        return ResponseEntity.ok(dto);
    }
}


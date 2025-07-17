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

    // Endpoint principal que retorna TODOS os dados para a tela
    @GetMapping("/metricas")
    public ResponseEntity<RelatorioDTO> buscarMetricas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        RelatorioDTO dto = new RelatorioDTO();
        dto.setTotalReclamacoes(service.contarTotalReclamacoes(inicio, fim));
        dto.setTotalPendentes(service.contarPendentes(inicio, fim));
        dto.setTempoMedioResolucao(service.calcularTempoMedioResolucao(inicio, fim));
        dto.setUnidadeMaisReclama(service.unidadeComMaisReclamacoes(inicio, fim));
        dto.setUltimasReclamacoes(service.ultimas10ReclamacoesAbertas());
        dto.setTopUnidadesQueMaisReclamam(service.topUnidadesQueMaisReclamam(inicio, fim));
        dto.setTopUnidadesMaisReclamadas(service.topUnidadesMaisReclamadas(inicio, fim));

        return ResponseEntity.ok(dto);
    }
}


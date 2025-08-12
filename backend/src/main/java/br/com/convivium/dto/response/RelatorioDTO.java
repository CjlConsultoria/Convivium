package br.com.convivium.dto.response;

import br.com.convivium.service.ReclamacaoService;
import lombok.Data;

import java.util.List;

@Data
public class RelatorioDTO {

    private long totalReclamacoes;
    private long totalPendentes;
    private double tempoMedioResolucao;
    private String unidadeMaisReclama;

    private List<ReclamacaoResumoDTO> ultimasReclamacoes;
    private List<ReclamacaoService.UnidadeQtdDTO> topUnidadesQueMaisReclamam;
    private List<ReclamacaoService.UnidadeQtdDTO> topUnidadesMaisReclamadas;

}


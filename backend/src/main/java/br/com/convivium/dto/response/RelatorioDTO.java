package br.com.convivium.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RelatorioDTO {

    private long totalReclamacoes;
    private long totalPendentes;
    private double tempoMedioResolucao;
    private String unidadeMaisReclama;

    private List<ReclamacaoResumoDTO> ultimasReclamacoes;
    private List<UnidadeQtdDTO> topUnidadesQueMaisReclamam;
    private List<UnidadeQtdDTO> topUnidadesMaisReclamadas;

}


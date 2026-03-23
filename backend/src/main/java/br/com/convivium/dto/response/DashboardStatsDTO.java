package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalEncomendas;
    private long encomendasDisponiveis;
    private long encomendasRetiradas;
    private long encomendasAguardando;
}
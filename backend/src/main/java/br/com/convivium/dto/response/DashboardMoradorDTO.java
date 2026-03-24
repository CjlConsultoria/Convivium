package br.com.convivium.dto.response;

import lombok.Data;

@Data
public class DashboardMoradorDTO {
    private long totalEncomendas;
    private long encomendasPendentes;
}
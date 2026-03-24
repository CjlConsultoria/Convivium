package br.com.convivium.service;

import br.com.convivium.entity.enums.StatusEncomenda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final ParcelService parcelService;

    public DashboardService(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatsForMorador(Long moradorId, Long empresaId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Contagem total de encomendas do morador neste condomínio específico
        long totalEncomendas = parcelService.countParcelsByMoradorAndEmpresa(moradorId, empresaId);
        
        // Contagem de encomendas disponíveis do morador neste condomínio específico
        long encomendasDisponiveis = parcelService.countParcelsByMoradorAndEmpresaAndStatus(
                moradorId, empresaId, StatusEncomenda.DISPONIVEL);
        
        // Contagem de encomendas retiradas do morador neste condomínio específico
        long encomendasRetiradas = parcelService.countParcelsByMoradorAndEmpresaAndStatus(
                moradorId, empresaId, StatusEncomenda.RETIRADA);
        
        // Contagem de encomendas aguardando do morador neste condomínio específico
        long encomendasAguardando = parcelService.countParcelsByMoradorAndEmpresaAndStatus(
                moradorId, empresaId, StatusEncomenda.AGUARDANDO);
        
        stats.put("totalEncomendas", totalEncomendas);
        stats.put("encomendasDisponiveis", encomendasDisponiveis);
        stats.put("encomendasRetiradas", encomendasRetiradas);
        stats.put("encomendasAguardando", encomendasAguardando);
        
        return stats;
    }
}
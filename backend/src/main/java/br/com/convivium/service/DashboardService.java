package br.com.convivium.service;

import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.repository.EncomendaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final EncomendaRepository encomendaRepository;

    public DashboardService(EncomendaRepository encomendaRepository) {
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStatsForMorador(Long moradorId, Long condoId) {
        Map<String, Long> stats = new HashMap<>();
        
        // Conta todas as encomendas do morador no condomínio específico
        long total = encomendaRepository.findByMoradorIdAndEmpresaId(moradorId, condoId, PageRequest.of(0, 1)).getTotalElements();
        
        // Conta encomendas aguardando do morador no condomínio específico
        long aguardando = encomendaRepository.findByMoradorIdAndEmpresaIdAndStatus(moradorId, condoId, StatusEncomenda.AGUARDANDO, PageRequest.of(0, 1)).getTotalElements();
        
        // Conta encomendas disponíveis do morador no condomínio específico
        long disponiveis = encomendaRepository.findByMoradorIdAndEmpresaIdAndStatus(moradorId, condoId, StatusEncomenda.DISPONIVEL, PageRequest.of(0, 1)).getTotalElements();
        
        // Conta encomendas retiradas do morador no condomínio específico
        long retiradas = encomendaRepository.findByMoradorIdAndEmpresaIdAndStatus(moradorId, condoId, StatusEncomenda.RETIRADA, PageRequest.of(0, 1)).getTotalElements();
        
        stats.put("total", total);
        stats.put("aguardando", aguardando);
        stats.put("disponiveis", disponiveis);
        stats.put("retiradas", retiradas);
        
        return stats;
    }
}
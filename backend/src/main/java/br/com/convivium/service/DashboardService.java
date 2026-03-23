package br.com.convivium.service;

import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.repository.EncomendaRepository;
import br.com.convivium.repository.ReclamacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final EncomendaRepository encomendaRepository;
    private final ReclamacaoRepository reclamacaoRepository;

    public DashboardService(EncomendaRepository encomendaRepository, ReclamacaoRepository reclamacaoRepository) {
        this.encomendaRepository = encomendaRepository;
        this.reclamacaoRepository = reclamacaoRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatsForMorador(Long condominiumId, Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // CORREÇÃO: Agora usa condominiumId para filtrar adequadamente
        Long totalEncomendas = encomendaRepository.countByCondominiumIdAndRecipientId(condominiumId, userId);
        Long encomendasDisponiveis = encomendaRepository.countByCondominiumIdAndRecipientIdAndStatusNot(condominiumId, userId, StatusEncomenda.RETIRADA);
        
        // Para reclamações, também filtra por condomínio
        Long totalReclamacoes = reclamacaoRepository.countByCondominiumIdAndComplainantId(condominiumId, userId);
        
        stats.put("totalEncomendas", totalEncomendas);
        stats.put("encomendasDisponiveis", encomendasDisponiveis);
        stats.put("totalReclamacoes", totalReclamacoes);
        
        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatsForAdmin(Long condominiumId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Stats administrativas filtradas por condomínio
        Long totalEncomendas = encomendaRepository.countByEmpresaId(condominiumId);
        Long encomendasPendentes = encomendaRepository.countByEmpresaIdAndStatus(condominiumId, StatusEncomenda.DISPONIVEL);
        Long totalReclamacoes = reclamacaoRepository.countByCondominiumId(condominiumId);
        
        stats.put("totalEncomendas", totalEncomendas);
        stats.put("encomendasPendentes", encomendasPendentes);
        stats.put("totalReclamacoes", totalReclamacoes);
        
        return stats;
    }
}
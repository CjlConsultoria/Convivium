package br.com.convivium.service;

import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.repository.EncomendaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParcelService {

    private final EncomendaRepository encomendaRepository;
    private final EncomendaService encomendaService;

    public ParcelService(EncomendaRepository encomendaRepository, EncomendaService encomendaService) {
        this.encomendaRepository = encomendaRepository;
        this.encomendaService = encomendaService;
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> getMyParcels(Long recipientId, Long condoId, Pageable pageable) {
        return encomendaRepository.findByMoradorIdAndEmpresaId(recipientId, condoId, pageable)
                .map(encomendaService::toDto);
    }
}
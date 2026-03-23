package br.com.convivium.service;

import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.repository.EncomendaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParcelService {

    private final EncomendaRepository parcelRepository;
    private final EncomendaService encomendaService;

    public ParcelService(EncomendaRepository parcelRepository, EncomendaService encomendaService) {
        this.parcelRepository = parcelRepository;
        this.encomendaService = encomendaService;
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> getMyParcels(Long condoId, Long userId, Pageable pageable) {
        // CORREÇÃO: Agora usa o condoId para filtrar adequadamente
        return parcelRepository.findByCondominiumIdAndRecipientId(condoId, userId, pageable)
                .map(encomendaService::toDto);
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> getMyParcelsByStatus(Long condoId, Long userId, StatusEncomenda status, Pageable pageable) {
        return parcelRepository.findByCondominiumIdAndRecipientIdAndStatus(condoId, userId, status, pageable)
                .map(encomendaService::toDto);
    }

    @Transactional(readOnly = true)
    public Long countMyParcelsExcludingStatus(Long condoId, Long userId, StatusEncomenda status) {
        return parcelRepository.countByCondominiumIdAndRecipientIdAndStatusNot(condoId, userId, status);
    }

    @Transactional(readOnly = true)
    public Long countMyParcels(Long condoId, Long userId) {
        return parcelRepository.countByCondominiumIdAndRecipientId(condoId, userId);
    }
}
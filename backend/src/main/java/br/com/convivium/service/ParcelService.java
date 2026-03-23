package br.com.convivium.service;

import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.entity.User;
import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.EncomendaRepository;
import br.com.convivium.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParcelService {

    private final EncomendaRepository encomendaRepository;
    private final UserRepository userRepository;
    private final EncomendaService encomendaService;

    public ParcelService(EncomendaRepository encomendaRepository,
                        UserRepository userRepository,
                        EncomendaService encomendaService) {
        this.encomendaRepository = encomendaRepository;
        this.userRepository = userRepository;
        this.encomendaService = encomendaService;
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> getMyParcels(Long recipientId, Long condoId, Pageable pageable) {
        // Validate that the user exists and belongs to the specified condominium
        User user = userRepository.findById(recipientId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));
        
        if (user.getEmpresa() == null || !user.getEmpresa().getId().equals(condoId)) {
            throw new ApiException.ForbiddenException("Usuário não pertence ao condomínio especificado");
        }
        
        // Filter by both recipientId AND condominiumId
        return encomendaRepository.findByMoradorIdAndEmpresaId(recipientId, condoId, pageable)
                .map(encomendaService::toDto);
    }

    @Transactional(readOnly = true)
    public long countMyParcels(Long recipientId, Long condoId) {
        // Validate that the user exists and belongs to the specified condominium
        User user = userRepository.findById(recipientId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));
        
        if (user.getEmpresa() == null || !user.getEmpresa().getId().equals(condoId)) {
            throw new ApiException.ForbiddenException("Usuário não pertence ao condomínio especificado");
        }
        
        return encomendaRepository.countByMoradorIdAndEmpresaId(recipientId, condoId);
    }

    @Transactional(readOnly = true)
    public long countMyParcelsByStatus(Long recipientId, Long condoId, StatusEncomenda status) {
        // Validate that the user exists and belongs to the specified condominium
        User user = userRepository.findById(recipientId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));
        
        if (user.getEmpresa() == null || !user.getEmpresa().getId().equals(condoId)) {
            throw new ApiException.ForbiddenException("Usuário não pertence ao condomínio especificado");
        }
        
        return encomendaRepository.countByMoradorIdAndEmpresaIdAndStatus(recipientId, condoId, status);
    }
}
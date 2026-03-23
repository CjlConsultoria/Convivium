package br.com.convivium.service;

import br.com.convivium.dto.response.DashboardStatsDTO;
import br.com.convivium.entity.User;
import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ParcelService parcelService;
    private final UserRepository userRepository;

    public DashboardService(ParcelService parcelService, UserRepository userRepository) {
        this.parcelService = parcelService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getStatsForMorador(Long moradorId, Long condoId) {
        // Validate that the user exists and belongs to the specified condominium
        User user = userRepository.findById(moradorId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));
        
        if (user.getEmpresa() == null || !user.getEmpresa().getId().equals(condoId)) {
            throw new ApiException.ForbiddenException("Usuário não pertence ao condomínio especificado");
        }
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Count parcels for this specific morador in this specific condominium only
        stats.setTotalEncomendas(parcelService.countMyParcels(moradorId, condoId));
        stats.setEncomendasDisponiveis(parcelService.countMyParcelsByStatus(moradorId, condoId, StatusEncomenda.DISPONIVEL));
        stats.setEncomendasRetiradas(parcelService.countMyParcelsByStatus(moradorId, condoId, StatusEncomenda.RETIRADA));
        stats.setEncomendasAguardando(parcelService.countMyParcelsByStatus(moradorId, condoId, StatusEncomenda.AGUARDANDO));
        
        return stats;
    }
}
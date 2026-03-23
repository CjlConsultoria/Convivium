package br.com.convivium.controller;

import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.entity.User;
import br.com.convivium.entity.enums.RoleType;
import br.com.convivium.service.AuthenticationService;
import br.com.convivium.service.ParcelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelService parcelService;
    private final AuthenticationService authenticationService;

    public ParcelController(ParcelService parcelService, AuthenticationService authenticationService) {
        this.parcelService = parcelService;
        this.authenticationService = authenticationService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        String cpf = (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails)
                ? ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername()
                : null;
        if (cpf == null) return null;
        return authenticationService.getUserDetails(cpf);
    }

    @GetMapping("/my-parcels")
    public ResponseEntity<Page<EncomendaDTO>> getMyParcels(
            @RequestParam Long condoId,
            Pageable pageable) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Ensure user can only access their own parcels or has appropriate role
        if (currentUser.getEmpresa() == null || !currentUser.getEmpresa().getId().equals(condoId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Page<EncomendaDTO> parcels = parcelService.getMyParcels(currentUser.getId(), condoId, pageable);
        return ResponseEntity.ok(parcels);
    }
}
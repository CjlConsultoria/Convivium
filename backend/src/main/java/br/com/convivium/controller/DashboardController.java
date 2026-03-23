package br.com.convivium.controller;

import br.com.convivium.dto.response.DashboardStatsDTO;
import br.com.convivium.entity.User;
import br.com.convivium.service.AuthenticationService;
import br.com.convivium.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthenticationService authenticationService;

    public DashboardController(DashboardService dashboardService, AuthenticationService authenticationService) {
        this.dashboardService = dashboardService;
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

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats(@RequestParam Long condoId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Ensure user can only access stats for their own condominium
        if (currentUser.getEmpresa() == null || !currentUser.getEmpresa().getId().equals(condoId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        DashboardStatsDTO stats = dashboardService.getStatsForMorador(currentUser.getId(), condoId);
        return ResponseEntity.ok(stats);
    }
}
package br.com.convivium.service;

import br.com.convivium.dto.UserMapper;
import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.dto.response.UserResponseDTO;
import br.com.convivium.entity.*;
import br.com.convivium.entity.enums.TipoToken;
import br.com.convivium.entity.specification.UsuarioSpecification;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TipoRepository tipoRepository;
    private final RoleRepository roleRepository;
    private final UserTokenRepository userTokenRepository;

    public UserService(UserRepository userRepository, TipoRepository tipoRepository, RoleRepository roleRepository, UserTokenRepository userTokenRepository) {
        this.userRepository = userRepository;
        this.tipoRepository = tipoRepository;
        this.roleRepository = roleRepository;
        this.userTokenRepository = userTokenRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public Page<User> listAll(Long empresaId, Pageable pageable) {
        return userRepository.findByEmpresaIdAndAtivoTrue(empresaId, pageable);
    }

    public List<Tipo> listTipo() {
        return tipoRepository.findAll();
    }

    public List<Role> listarPermissao() {
        return roleRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public Optional<User> buscarUserId(Long id) {
        return userRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public User buscarPorCpfECondominio(String cpf, Long idCondominio) {
        return userRepository.findByCpfAndEmpresaId(cpf, idCondominio);
    }

    public User buscarPorId(Long id) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id).orElse(null);
        
        // Usuário só pode acessar seus próprios dados ou ser admin
        if (targetUser != null && !canAccessUserData(currentUser, targetUser)) {
            throw new ApiException.ForbiddenException("Acesso negado aos dados do usuário.");
        }
        
        return targetUser;
    }

    public Optional<UserResponseDTO> buscarPorIdComDTO(Long id) {
        User currentUser = getCurrentUser();
        Optional<User> targetUserOpt = userRepository.findByIdWithRelations(id);
        
        if (targetUserOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User targetUser = targetUserOpt.get();
        
        if (!canAccessUserData(currentUser, targetUser)) {
            return Optional.of(UserMapper.toPublicDTO(targetUser));
        }
        
        // Se é admin, retorna versão com dados mascarados
        if (isAdmin(currentUser) && !currentUser.getId().equals(targetUser.getId())) {
            return Optional.of(UserMapper.toAdminDTO(targetUser));
        }
        
        // Se é o próprio usuário, retorna dados completos via AuthDTO convertido
        return Optional.of(UserMapper.toPublicDTO(targetUser));
    }

    public void ativarConta(Long idUsuario, String senhaCriptografada, String token) {
        // Buscar o token do tipo ATIVACAO
        UserToken userToken = userTokenRepository.findByTokenAndTipo(token, TipoToken.ATIVACAO_CONTA)
                .orElseThrow(() -> new ApiException.NotFoundException("Token de ativação inválido."));

        if (userToken.isUsed()) {
            throw new ApiException.BadRequestException("Token já utilizado.");
        }

        if (userToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException.BadRequestException("Token expirado.");
        }

        User usuario = userToken.getUser();

        if (!usuario.getId().equals(idUsuario)) {
            throw new ApiException.BadRequestException("Token não corresponde ao usuário.");
        }

        if (!"pendente_ativacao".equals(usuario.getStatus())) {
            throw new ApiException.BadRequestException("Usuário já está ativado ou em estado inválido.");
        }

        usuario.setPassword(senhaCriptografada);
        usuario.setStatus("ativo");
        usuario.setAtivo(true);
        userRepository.save(usuario);

        // Marcar token como usado
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
    }

    public Page<UserResponseDTO> listarUsuariosSemSenha(Long idEmpresa, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<User> usuarios = userRepository.findAllByEmpresaId(idEmpresa, pageable);
        
        return usuarios.map(user -> {
            if (isAdmin(currentUser)) {
                return UserMapper.toAdminDTO(user);
            } else {
                return UserMapper.toPublicDTO(user);
            }
        });
    }

    public Page<UserResponseDTO> listarComFiltro(UsuarioFiltroDTO filtro, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        if (!isAdmin(currentUser)) {
            throw new ApiException.ForbiddenException("Apenas administradores podem realizar buscas filtradas.");
        }
        
        Specification<User> spec = UsuarioSpecification.filtrarPorNome(filtro);
        Page<User> page = userRepository.findAll(spec, pageable);
        
        return page.map(UserMapper::toAdminDTO);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException.UnauthorizedException("Usuário não autenticado.");
        }
        
        String cpf = authentication.getName();
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado."));
    }
    
    private boolean canAccessUserData(User currentUser, User targetUser) {
        // O próprio usuário pode acessar seus dados
        if (currentUser.getId().equals(targetUser.getId())) {
            return true;
        }
        
        // Administradores podem acessar dados de usuários da mesma empresa
        return isAdmin(currentUser) && 
               currentUser.getEmpresa().getId().equals(targetUser.getEmpresa().getId());
    }
    
    private boolean isAdmin(User user) {
        return user.getRole() != null && 
               ("ADMIN".equals(user.getRole().getName()) || "ADMINISTRATIVO".equals(user.getRole().getName()));
    }
}
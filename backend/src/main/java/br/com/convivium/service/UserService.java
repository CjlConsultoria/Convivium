package br.com.convivium.service;

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

    public Page<User> listAll(Long empresaId, Pageable pageable) {
        return userRepository.findByEmpresaIdAndAtivoTrue(empresaId, pageable);
    }

    public List<Tipo> listTipo() {
        return tipoRepository.findAll();
    }

    public List<Role> listarPermissao() {
        return roleRepository.findAll();
    }

    public Optional<User> buscarUserId(Long id) {
        return userRepository.findById(id);
    }

    public User buscarPorCpfECondominio(String cpf, Long idCondominio) {
        return userRepository.findByCpfAndEmpresaId(cpf, idCondominio);
    }

    public User buscarPorId(Long id) {
        return userRepository.findById(id).orElse(null);
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
        Page<User> usuarios = userRepository.findAllByEmpresaId(idEmpresa, pageable);
        return usuarios.map(this::mapToDTO);
    }

    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCpf(user.getCpf());
        dto.setTelefone(user.getTelefone());
        dto.setAtivo(user.getAtivo());
        dto.setSobrenome(user.getSobrenome());
        dto.setGenero(user.getGenero());
        dto.setCep(user.getCep());
        dto.setLogradouro(user.getLogradouro());
        dto.setCidade(user.getCidade());
        dto.setEstado(user.getEstado());
        dto.setBairro(user.getBairro());
        dto.setNumero(user.getNumero());
        dto.setComplemento(user.getComplemento());
        dto.setAlerta(user.getAlerta());
        dto.setBloco(user.getBloco());
        dto.setApartamento(user.getApartamento());
        dto.setVagaCarro(user.getVagaCarro());
        dto.setVagaMoto(user.getVagaMoto());

        if (user.getRole() != null) {
            dto.setRole(user.getRole().getName());
        }
        if (user.getTipo() != null) {
            dto.setTipo(user.getTipo().getName());
        }
        if (user.getEmpresa() != null) {
            dto.setEmpresa(user.getEmpresa().getName());
        }

        return dto;
    }

    public Page<UserResponseDTO> listarComFiltro(UsuarioFiltroDTO filtro, Pageable pageable) {
        Specification<User> spec = UsuarioSpecification.filtrarPorNomeECpf(filtro);
        Page<User> page = userRepository.findAll(spec, pageable);
        return page.map(this::mapToDTO);
    }



}

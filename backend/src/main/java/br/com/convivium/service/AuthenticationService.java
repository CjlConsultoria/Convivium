package br.com.convivium.service;

import br.com.convivium.dto.request.RegisterRequest;
import br.com.convivium.dto.request.UserUpdateRequest;
import br.com.convivium.entity.*;
import br.com.convivium.entity.enums.EmpresaType;
import br.com.convivium.entity.enums.RoleType;
import br.com.convivium.entity.enums.TipoCargo;
import br.com.convivium.entity.enums.TipoTemplateEmail;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.*;
import br.com.convivium.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoRepository tipoRepository;
    private final EmailService emailService;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 BCryptPasswordEncoder passwordEncoder,
                                 JwtTokenUtil jwtTokenUtil,
                                 RoleRepository roleRepository,
                                 TokenRepository tokenRepository, EmpresaRepository empresaRepository, TipoRepository tipoRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.empresaRepository = empresaRepository;
        this.tipoRepository = tipoRepository;
        this.emailService = emailService;
    }

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado: " + username));

        if (!Boolean.TRUE.equals(user.getAtivo())) {
            throw new ApiException.ForbiddenException("Usuário está inativo.");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenUtil.generateToken(user);
        } else {
            throw new ApiException.UnauthorizedException("Credenciais inválidas.");
        }
    }

    public String generateToken(String cpf, String password) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado pelo CPF: " + cpf));

        if (!Boolean.TRUE.equals(user.getAtivo())) {
            throw new ApiException.ForbiddenException("Usuário está inativo.");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenUtil.generateToken(user);
        } else {
            throw new ApiException.UnauthorizedException("CPF ou senha inválidos.");
        }
    }

    public void register(RegisterRequest registerRequest) {
        userRepository.findByCpf(registerRequest.getCpf()).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getAtivo())) {
                throw new ApiException.ConflictException("Usuário já registrado e ativo.");
            }
            user.setAtivo(true);
            user.setStatus("pendente_ativacao");
            updateUserFromRegisterRequest(user, registerRequest);
            userRepository.save(user);
            if(registerRequest.getAlerta()) {
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("usuario", user); // Trocar "usuario" em vez de "denunciante"

                emailService.enviarEmailComTemplate(
                        user.getEmail(),
                        TipoTemplateEmail.BEM_VINDO, // Enum para identificar esse novo template
                        templateVariables
                );
            }
            throw new ApiException.BadRequestException("Usuário reativado com sucesso.");
        });
        User user = new User();
        user.setAtivo(true);
        updateUserFromRegisterRequest(user, registerRequest);

        EmpresaType empresaType = EmpresaType.fromId(registerRequest.getEmpresa());
        Empresa empresa = empresaRepository.findById((long) empresaType.getId())
                .orElseThrow(() -> new ApiException.NotFoundException("Empresa não encontrado com ID: " + empresaType.getId()));

        RoleType roleType = RoleType.fromId(registerRequest.getRole());
        Role role = roleRepository.findById((long) roleType.getId())
                .orElseThrow(() -> new ApiException.NotFoundException("Perfil não encontrado com ID: " + roleType.getId()));

        TipoCargo tipoCargo = TipoCargo.fromId(registerRequest.getTipoUsuario());
        Tipo tipo = tipoRepository.findById((long) tipoCargo.getId())
                .orElseThrow(() -> new ApiException.NotFoundException("Cargo não encontrado com ID: " + tipoCargo.getId()));

        user.setStatus("pendente_ativacao");
        user.setTipo(tipo);
        user.setEmpresa(empresa);
        user.setRole(role);
        userRepository.save(user);



            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("username", user.getUsername());
            usuarioMap.put("cpf", user.getCpf());
            usuarioMap.put("email", user.getEmail());

            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("usuario", usuarioMap);
            templateVariables.put("empresaId", user.getEmpresa().getId().toString());
            templateVariables.put("empresaNome", user.getEmpresa().getCodigoPublico());

            emailService.enviarEmailComTemplate(
                    user.getEmail(),
                    TipoTemplateEmail.BEM_VINDO,
                    templateVariables
            );

    }

    private void updateUserFromRegisterRequest(User user, RegisterRequest req) {
        user.setCpf(req.getCpf().replaceAll("\\D", ""));
        user.setUsername(req.getUsername());
        user.setSobrenome(req.getSobrenome());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setTelefone(req.getTelefone());
        user.setCep(req.getCep());
        user.setLogradouro(req.getLogradouro());
        user.setCidade(req.getCidade());
        user.setEstado(req.getEstado());
        user.setBairro(req.getBairro());
        user.setNumero(req.getNumero());
        user.setComplemento(req.getComplemento());
        user.setGenero(req.getGenero());
        user.setAlerta(req.getAlerta());
        user.setApartamento(req.getApartamento());
        user.setBloco(req.getBloco());
        user.setVagaCarro(req.getVagaCarro());
        user.setVagaMoto(req.getVagaMoto());
    }

    public User getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado: " + username));
    }

    public Map<String, Object> forgotPassword(String emailCpf) {
        User user;
        if (emailCpf.contains("@")) {
            user = userRepository.findByEmail(emailCpf)
                    .orElseThrow(() -> new ApiException.NotFoundException("Usuário com esse e-mail não encontrado."));
        } else {
            user = userRepository.findByCpf(emailCpf)
                    .orElseThrow(() -> new ApiException.NotFoundException("Usuário com esse CPF não encontrado."));
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(1),
                false
        );

        tokenRepository.save(resetToken);

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", user.getUsername());

        Map<String, Object> variables = new HashMap<>();
        variables.put("usuario", usuario);
        variables.put("token", token);

        emailService.enviarEmailComTemplate(
                user.getEmail(),
                TipoTemplateEmail.ESQUECEU_SENHA,
                variables
        );

        return Map.of(
                "mensagem", "E-mail de redefinição enviado com sucesso.",
                "email", user.getEmail()
        );
    }

    public boolean resetarSenha(String token, String novaSenha) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException.NotFoundException("Token de redefinição de senha inválido."));

        if (resetToken.isUsed()) {
            throw new ApiException.BadRequestException("Token já foi utilizado.");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException.BadRequestException("Token expirado.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(novaSenha));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    public boolean updateUserData(String id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado."));

        RoleType roleType = switch (userUpdateRequest.getTipo()) {
            case 1 -> RoleType.ADMIN;
            case 2 -> RoleType.ADMINISTRATIVO;
            default -> throw new ApiException.BadRequestException("Tipo de usuário inválido.");
        };

        Role role = roleRepository.findById((long) roleType.getId())
                .orElseThrow(() -> new ApiException.NotFoundException("Perfil não encontrado: " + roleType));

        // Atualiza dados básicos
        user.setUsername(userUpdateRequest.getUsername());
        user.setSobrenome(userUpdateRequest.getSobrenome());
        user.setEmail(userUpdateRequest.getEmail());
        user.setCpf(userUpdateRequest.getCpf());
        user.setTelefone(userUpdateRequest.getTelefone());
        user.setCep(userUpdateRequest.getCep());
        user.setLogradouro(userUpdateRequest.getLogradouro());
        user.setCidade(userUpdateRequest.getCidade());
        user.setEstado(userUpdateRequest.getEstado());
        user.setBairro(userUpdateRequest.getBairro());
        user.setNumero(userUpdateRequest.getNumero());
        user.setComplemento(userUpdateRequest.getComplemento());
        user.setGenero(userUpdateRequest.getGenero());
        user.setAlerta(userUpdateRequest.getAlerta());
        user.setRole(role);

        userRepository.save(user);
        return true;
    }

    public void deleteUserById(String id) {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(id));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAtivo(false);
            user.setStatus("INATIVO");
            userRepository.save(user);
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public Optional<User> buscarPorCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }
}

package br.com.convivium.service;

import br.com.convivium.entity.Role;
import br.com.convivium.entity.Tipo;
import br.com.convivium.entity.User;
import br.com.convivium.repository.RoleRepository;
import br.com.convivium.repository.TipoRepository;
import br.com.convivium.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TipoRepository tipoRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, TipoRepository tipoRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.tipoRepository = tipoRepository;
        this.roleRepository = roleRepository;
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

    public void ativarConta(Long idUsuario, String senhaCriptografada) {
        User usuario = buscarPorId(idUsuario);
        if (usuario != null && "pendente_ativacao".equals(usuario.getStatus())) {
            usuario.setPassword(senhaCriptografada);
            usuario.setStatus("ativo");
            usuario.setAtivo(true);
            userRepository.save(usuario);
        }
    }

}

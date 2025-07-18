package br.com.convivium.repository;

import br.com.convivium.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByCpfAndAtivoTrue(String cpf);
    Page<User> findByEmpresaIdAndAtivoTrue(Long empresaId, Pageable pageable);
    Optional<User> findByEmail(String emailCpf);

    User findByCpfAndEmpresaId(String cpf, Long empresaId);

    long countByEmpresaIdAndAtivoTrue(Long id);

    boolean existsByEmpresaId(Long id);

    Page<User> findAllByEmpresaId(Long idEmpresa, Pageable pageable);
}


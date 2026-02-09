package br.com.convivium.repository;

import br.com.convivium.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    
    @EntityGraph(attributePaths = {"role", "empresa", "tipo"})
    Optional<User> findByCpf(String cpf);
    
    @EntityGraph(attributePaths = {"role", "empresa", "tipo"})
    Optional<User> findByCpfAndAtivoTrue(String cpf);
    
    @EntityGraph(attributePaths = {"role", "tipo"})
    Page<User> findByEmpresaIdAndAtivoTrue(Long empresaId, Pageable pageable);
    
    Optional<User> findByEmail(String emailCpf);

    @EntityGraph(attributePaths = {"role", "empresa", "tipo"})
    User findByCpfAndEmpresaId(String cpf, Long empresaId);

    long countByEmpresaIdAndAtivoTrue(Long id);

    boolean existsByEmpresaId(Long id);

    @EntityGraph(attributePaths = {"role", "tipo"})
    Page<User> findAllByEmpresaId(Long idEmpresa, Pageable pageable);
    
    @EntityGraph(attributePaths = {"role", "empresa", "tipo"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithRelations(@Param("id") Long id);
}


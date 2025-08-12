package br.com.convivium.repository;

import br.com.convivium.entity.Role;
import br.com.convivium.entity.Tipo;
import br.com.convivium.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByName(RoleType name);

}

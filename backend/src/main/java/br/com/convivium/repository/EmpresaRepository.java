package br.com.convivium.repository;

import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Role;
import br.com.convivium.entity.enums.EmpresaType;
import br.com.convivium.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
    Optional<Empresa> findByName(String name);
    Optional<Empresa> findByName(EmpresaType name);
    Empresa findByCodigoPublico(String codigoPublico);

    boolean existsByCnpj(String cnpj);
}

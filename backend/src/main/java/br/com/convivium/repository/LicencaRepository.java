package br.com.convivium.repository;

import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Licenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LicencaRepository extends JpaRepository<Licenca, Long>, JpaSpecificationExecutor<Licenca> {
    Optional<Licenca> findByEmpresaId(Long empresaId);

    Optional<Object> findByEmpresa(Empresa empresa);

    Optional<Licenca> findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(Long empresaId);

}



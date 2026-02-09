package br.com.convivium.repository;

import br.com.convivium.entity.CondominioInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CondominioInfoRepository extends JpaRepository<CondominioInfo, Long> {

    @Query("SELECT c FROM CondominioInfo c JOIN FETCH c.condominio WHERE c.condominio.id = :empresaId")
    Optional<CondominioInfo> findByCondominio_Id(@Param("empresaId") Long empresaId);
}

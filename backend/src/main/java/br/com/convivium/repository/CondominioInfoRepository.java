package br.com.convivium.repository;

import br.com.convivium.entity.CondominioInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CondominioInfoRepository extends JpaRepository<CondominioInfo, Long> {

    Optional<CondominioInfo> findByCondominio_Id(Long empresaId);
}

package br.com.convivium.repository;

import br.com.convivium.entity.Encomenda;
import br.com.convivium.entity.enums.StatusEncomenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {

    Optional<Encomenda> findByCodigoRetiradaAndEmpresaId(String codigoRetirada, Long empresaId);

    Page<Encomenda> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<Encomenda> findByMoradorIdAndEmpresaId(Long moradorId, Long empresaId, Pageable pageable);

    Page<Encomenda> findByEmpresaIdAndStatus(Long empresaId, StatusEncomenda status, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Encomenda e WHERE e.morador.id = :moradorId AND e.empresa.id = :empresaId")
    long countByMoradorIdAndEmpresaId(@Param("moradorId") Long moradorId, @Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(e) FROM Encomenda e WHERE e.morador.id = :moradorId AND e.empresa.id = :empresaId AND e.status = :status")
    long countByMoradorIdAndEmpresaIdAndStatus(@Param("moradorId") Long moradorId, @Param("empresaId") Long empresaId, @Param("status") StatusEncomenda status);
}
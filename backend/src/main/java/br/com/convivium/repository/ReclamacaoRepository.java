package br.com.convivium.repository;

import br.com.convivium.entity.Reclamacao;
import br.com.convivium.entity.enums.StatusReclamacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReclamacaoRepository extends JpaRepository<Reclamacao, Long>, JpaSpecificationExecutor<Reclamacao> {
    List<Reclamacao> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Reclamacao> findTop10ByStatusOrderByDataCriacaoDesc(StatusReclamacao status);
}

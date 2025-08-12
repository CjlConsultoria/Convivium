package br.com.convivium.repository;

import br.com.convivium.entity.AcaoReclamacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcaoReclamacaoRepository extends JpaRepository<AcaoReclamacao, Long> {

    List<AcaoReclamacao> findByReclamacaoIdOrderByIdAsc(Long reclamacaoId);

}

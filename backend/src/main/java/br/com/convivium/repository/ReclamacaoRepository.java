package br.com.convivium.repository;

import br.com.convivium.entity.Reclamacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamacaoRepository extends JpaRepository<Reclamacao, Long>, JpaSpecificationExecutor<Reclamacao> {
    // Aqui pode colocar métodos customizados se quiser
}

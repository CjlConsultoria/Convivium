package br.com.convivium.repository;

import br.com.convivium.entity.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    Optional<Tipo> findByName(String name);
}

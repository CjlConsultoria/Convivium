package br.com.convivium.repository;

import br.com.convivium.entity.UserToken;
import br.com.convivium.entity.enums.TipoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenAndTipo(String token, TipoToken tipo);
}


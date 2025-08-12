package br.com.convivium.entity.specification;

import br.com.convivium.entity.Licenca;
import br.com.convivium.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class LicencaSpecification {

    public static Specification<Licenca> filter(String empresaNome, String usuarioNome, String cpf) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (empresaNome != null && !empresaNome.isEmpty()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(root.get("empresa").get("name")),
                        "%" + empresaNome.toLowerCase() + "%"
                ));
            }

            if ((usuarioNome != null && !usuarioNome.isEmpty()) || (cpf != null && !cpf.isEmpty())) {
                Join<Object, User> userJoin = root.join("empresa").join("users", JoinType.LEFT);

                if (usuarioNome != null && !usuarioNome.isEmpty()) {
                    predicate = cb.and(predicate, cb.like(
                            cb.lower(userJoin.get("username")),
                            "%" + usuarioNome.toLowerCase() + "%"
                    ));
                }

                if (cpf != null && !cpf.isEmpty()) {
                    predicate = cb.and(predicate, cb.equal(userJoin.get("cpf"), cpf));
                }
            }

            // Você pode adicionar esse filtro caso deseje retornar apenas licenças ativas por padrão:
            predicate = cb.and(predicate, cb.isTrue(root.get("ativa")));

            return predicate;
        };
    }

}


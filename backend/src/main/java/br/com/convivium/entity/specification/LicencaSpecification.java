package br.com.convivium.entity.specification;

import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Licenca;
import br.com.convivium.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class LicencaSpecification {

    public static Specification<Licenca> filter(String empresaNome, String usuarioNome, String cpf) {
        return (root, query, cb) -> {
            Join<Licenca, Empresa> empresaJoin = root.join("empresa", JoinType.LEFT);
            Join<Empresa, User> responsavelJoin = empresaJoin.join("usuarioResponsavel", JoinType.LEFT);
            responsavelJoin.join("role", JoinType.LEFT);
            if (query.getResultType() != null && query.getResultType() != Long.class) {
                query.distinct(true);
            }

            var predicate = cb.conjunction();

            if (empresaNome != null && !empresaNome.isEmpty()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(empresaJoin.get("name")),
                        "%" + empresaNome.toLowerCase() + "%"
                ));
            }

            if (usuarioNome != null && !usuarioNome.isEmpty()) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(responsavelJoin.get("username")),
                        "%" + usuarioNome.toLowerCase() + "%"
                ));
            }

            if (cpf != null && !cpf.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(responsavelJoin.get("cpf"), cpf));
            }

            predicate = cb.and(predicate, cb.isTrue(root.get("ativa")));

            return predicate;
        };
    }

}


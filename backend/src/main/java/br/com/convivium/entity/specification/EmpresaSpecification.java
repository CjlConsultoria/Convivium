package br.com.convivium.entity.specification;

import br.com.convivium.entity.Empresa;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class EmpresaSpecification {

    public static Specification<Empresa> filterByNomeOrCnpj(String nome, String cnpj) {
        return (root, query, builder) -> {
            List<Predicate> orPredicates = new ArrayList<>();

            if (nome != null && !nome.isBlank()) {
                orPredicates.add(builder.like(builder.lower(root.get("name")), "%" + nome.toLowerCase() + "%"));
            }

            if (cnpj != null && !cnpj.isBlank()) {
                orPredicates.add(builder.like(builder.lower(root.get("cnpj")), "%" + cnpj.toLowerCase() + "%"));
            }

            // Nenhum filtro preenchido
            if (orPredicates.isEmpty()) {
                return builder.conjunction();
            }

            // Usa OR entre os campos
            return builder.or(orPredicates.toArray(new Predicate[0]));
        };
    }
}


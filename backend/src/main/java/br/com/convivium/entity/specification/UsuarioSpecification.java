package br.com.convivium.entity.specification;

import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.entity.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

public class UsuarioSpecification {

    // Modified method to include empresaId filtering for multi-tenant isolation
    public static Specification<User> filtrarPorNomeECpf(UsuarioFiltroDTO filtro) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // CRITICAL: Always filter by empresaId for multi-tenant isolation
            // This ensures users can only see users from their own company/condominium
            if (filtro.getEmpresaId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("empresaId"), filtro.getEmpresaId()));
            } else {
                // If no empresaId is provided, return no results for security
                // This prevents enumeration when empresaId is missing
                predicate = cb.and(predicate, cb.disjunction()); // Always false condition
            }

            // OR entre nome e CPF (um campo único no frontend pode cair aqui)
            if ((filtro.getNome() != null && !filtro.getNome().isBlank()) ||
                    (filtro.getCpf() != null && !filtro.getCpf().isBlank())) {

                String termo = filtro.getNome() != null && !filtro.getNome().isBlank()
                        ? filtro.getNome()
                        : filtro.getCpf();

                Predicate porNome = cb.like(cb.lower(root.get("username")), "%" + termo.toLowerCase() + "%");
                Predicate porCpf = cb.like(cb.lower(root.get("cpf")), "%" + termo.toLowerCase() + "%");

                predicate = cb.and(predicate, cb.or(porNome, porCpf));
            }

            // Sempre trazer apenas usuários ativos
            predicate = cb.and(predicate, cb.isTrue(root.get("ativo")));

            return predicate;
        };
    }
}




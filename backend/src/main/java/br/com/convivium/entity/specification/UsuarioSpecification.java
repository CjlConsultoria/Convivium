package br.com.convivium.entity.specification;

import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.entity.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

public class UsuarioSpecification {

    public static Specification<User> filtrarPorNomeECpf(UsuarioFiltroDTO filtro) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

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



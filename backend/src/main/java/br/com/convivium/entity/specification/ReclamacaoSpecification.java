package br.com.convivium.entity.specification;

import br.com.convivium.dto.request.ReclamacaoFiltroDTO;
import br.com.convivium.entity.Reclamacao;
import br.com.convivium.entity.enums.StatusReclamacao;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class ReclamacaoSpecification {

    public static Specification<Reclamacao> filtro(ReclamacaoFiltroDTO filtro) {
        return (Root<Reclamacao> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate p = cb.conjunction();

            Join<?, ?> usuario = root.join("usuario", JoinType.LEFT);

            if (filtro.getApartamento() != null && !filtro.getApartamento().isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(usuario.get("apartamento")), "%" + filtro.getApartamento().toLowerCase() + "%"));
            }

            if (filtro.getBloco() != null && !filtro.getBloco().isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(usuario.get("bloco")), "%" + filtro.getBloco().toLowerCase() + "%"));
            }

            if (filtro.getNomeUsuario() != null && !filtro.getNomeUsuario().isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(usuario.get("username")), "%" + filtro.getNomeUsuario().toLowerCase() + "%"));
            }

            if (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("tipo")), "%" + filtro.getTipo().toLowerCase() + "%"));
            }

            if (filtro.getDescricao() != null && !filtro.getDescricao().isEmpty()) {
                p = cb.and(p, cb.like(cb.lower(root.get("detalhes")), "%" + filtro.getDescricao().toLowerCase() + "%"));
            }

            if (filtro.getStatus() != null) {
                p = cb.and(p, cb.equal(root.get("status"), filtro.getStatus()));
            }

            if (filtro.getDataInicio() != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("dataCriacao"), filtro.getDataInicio().atStartOfDay()));
            }

            if (filtro.getDataFim() != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("dataCriacao"), filtro.getDataFim().atTime(23, 59, 59)));
            }

            return p;
        };
    }

}

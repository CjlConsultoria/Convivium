package br.com.convivium.entity.specification;

import br.com.convivium.dto.request.UsuarioFiltroDTO;
import br.com.convivium.entity.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;

public class UsuarioSpecification {

    public static Specification<User> filtrarPorNome(UsuarioFiltroDTO filtro) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Busca apenas por nome/sobrenome - CPF removido por segurança
            if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
                String termo = filtro.getNome().toLowerCase();
                
                Predicate porNome = cb.like(cb.lower(root.get("username")), "%" + termo + "%");
                Predicate porSobrenome = cb.like(cb.lower(root.get("sobrenome")), "%" + termo + "%");
                
                predicate = cb.and(predicate, cb.or(porNome, porSobrenome));
            }

            // Sempre trazer apenas usuários ativos
            predicate = cb.and(predicate, cb.isTrue(root.get("ativo")));

            return predicate;
        };
    }
    
    // Specification específica para administradores que precisam buscar por CPF
    public static Specification<User> filtrarPorCpfAdmin(String cpf) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            
            if (cpf != null && !cpf.isBlank()) {
                // Remove qualquer formatação do CPF
                String cpfLimpo = cpf.replaceAll("[^0-9]", "");
                predicate = cb.and(predicate, cb.equal(root.get("cpf"), cpfLimpo));
            }
            
            predicate = cb.and(predicate, cb.isTrue(root.get("ativo")));
            return predicate;
        };
    }
}
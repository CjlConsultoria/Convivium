package br.com.convivium.entity.enums;

/**
 * Perfis de acesso do sistema (RBAC).
 * ADMIN = super admin (licenças, todos condomínios).
 * SINDICO / SUB_SINDICO / CONSELHEIRO = gestão do condomínio.
 * MORADOR = apenas suas ações e encomendas.
 * ZELADOR / SEGURANCA / PORTARIA = funções com permissões específicas (ex: validar encomenda).
 */
public enum RoleType {
    ADMIN(1L, "ADMIN"),
    ADMINISTRATIVO(2L, "ADMINISTRATIVO"),
    USUARIO(3L, "USUARIO"),
    SINDICO(4L, "SINDICO"),
    SUB_SINDICO(5L, "SUB_SINDICO"),
    CONSELHEIRO(6L, "CONSELHEIRO"),
    MORADOR(7L, "MORADOR"),
    ZELADOR(8L, "ZELADOR"),
    SEGURANCA(9L, "SEGURANCA"),
    PORTARIA(10L, "PORTARIA");

    private final Long id;
    private final String nome;

    RoleType(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static RoleType fromId(Long id) {
        for (RoleType type : RoleType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("ID de RoleType inválido: " + id);
    }

    public static RoleType fromNome(String nome) {
        if (nome == null) return null;
        for (RoleType type : RoleType.values()) {
            if (type.getNome().equalsIgnoreCase(nome)) {
                return type;
            }
        }
        return null;
    }

    public boolean isGestaoCondominio() {
        return this == ADMIN || this == ADMINISTRATIVO || this == SINDICO || this == SUB_SINDICO || this == CONSELHEIRO;
    }

    public boolean podeValidarEncomenda() {
        return this == PORTARIA || this == SEGURANCA || this == ZELADOR || this == SINDICO || this == SUB_SINDICO || this == ADMIN || this == ADMINISTRATIVO;
    }
}

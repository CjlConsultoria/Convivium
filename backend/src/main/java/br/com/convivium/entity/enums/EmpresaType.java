package br.com.convivium.entity.enums;

public enum EmpresaType {
    ADMIN(1L, "ADMIN"),
    WATCHCAR(2L, "WATCHCAR"),
    CJL_CONSULTORIA(3L, "CJL CONSULTORIA");

    private final Long id;
    private final String nome;

    EmpresaType(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    // 🔥 Este é o método que estava faltando!
    public static EmpresaType fromId(Long id) {
        for (EmpresaType type : EmpresaType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("ID de EmpresaType inválido: " + id);
    }

    public static EmpresaType fromNome(String nome) {
        for (EmpresaType type : EmpresaType.values()) {
            if (type.getNome().equalsIgnoreCase(nome)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Nome de EmpresaType inválido: " + nome);
    }
}

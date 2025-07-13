package br.com.convivium.entity.enums;

public enum TipoCargo {
    SINDICO(1L, "Síndico"),
    CONSELHEIRO(2L, "Conselheiro"),
    ZELADOR(3L, "Zelador"),
    ADMINISTRATIVO(4L, "Administrativo"),
    MORADOR(5L, "Morador"),
    FUNCIONARIO(6L, "Funcionário"),
    SEGURANCA(7L, "Segurança"),
    PORTARIA(8L, "Portaria"),
    VIGILANTE(9L, "Vigilante"),
    LIMPEZA(10L, "Limpeza"),
    OUTRO(99L, "Outro");

    private final Long id;
    private final String descricao;

    TipoCargo(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    // Método para buscar enum pelo ID
    public static TipoCargo fromId(Long id) {
        for (TipoCargo cargo : values()) {
            if (cargo.id.equals(id)) {
                return cargo;
            }
        }
        return null;
    }
}
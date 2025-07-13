package br.com.convivium.entity.enums;

public enum TipoTemplateEmail {
    BEM_VINDO("bem-vindo", "Bem vindo!"),
    ESQUECEU_SENHA("esqueci-senha", "Redefinicao de senha"),
    ATUALIZACAO("atualizacao", "Novidade na sua reclamacao / denuncia"),
    NEWSLESTER("newslester", "Noticias");


    private final String templateNome;
    private final String titulo;

    TipoTemplateEmail(String templateNome, String titulo) {
        this.templateNome = templateNome;
        this.titulo = titulo;
    }

    public String getTemplateNome() {
        return templateNome;
    }

    public String getTitulo() {
        return titulo;
    }
}


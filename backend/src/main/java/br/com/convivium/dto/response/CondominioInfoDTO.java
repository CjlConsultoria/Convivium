package br.com.convivium.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CondominioInfoDTO {
    private Long id;
    private Long empresaId;
    private String nomeCondominio;

    private String horarioPiscina;
    private String horarioAcademia;
    private String horarioChurrasco;
    private String horarioSalaoFestas;
    private String horarioQuadra;
    private String horarioElevador;
    private String horarioPlayground;

    private String telefonePortaria;
    private String contatoSindico;
    private String contatoAdministradora;

    private String horarioBarulho;
    private String regrasGerais;
    private String regrasGaragem;
    private String regrasPiscina;
    private String regrasChurrasqueira;
    private String regrasSalaoFestas;
    private String regrasQuadra;

    private String limpeza;
    private String manutencao;
    private String iluminacao;
    private String agua;
    private String coletaLixo;

    private String boletoLink;
    private String formaPagamento;
    private String taxaCondominio;

    private String reservaLink;
    private String politicaReservas;

    private String estacionamentoInfo;
    private String visitantesInfo;
    private String animaisInfo;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}

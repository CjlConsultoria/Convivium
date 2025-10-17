package br.com.convivium.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONDOMINIO_INFO", schema = "convivium")
@Getter
@Setter
public class CondominioInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "condominio_info_seq")
    @SequenceGenerator(
            name = "condominio_info_seq",
            sequenceName = "convivium.TB_CONDOMINIO_INFO_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "EMPRESA_ID", nullable = false, unique = true)
    private Empresa condominio;

    // === Horários das áreas comuns ===
    private String horarioPiscina;
    private String horarioAcademia;
    private String horarioChurrasco;
    private String horarioSalaoFestas;
    private String horarioQuadra;
    private String horarioElevador;
    private String horarioPlayground;

    // === Contatos e responsáveis ===
    private String telefonePortaria;
    private String contatoSindico;
    private String contatoAdministradora;

    // === Regras e políticas ===
    private String horarioBarulho;
    private String regrasGerais;
    private String regrasGaragem;
    private String regrasPiscina;
    private String regrasChurrasqueira;
    private String regrasSalaoFestas;
    private String regrasQuadra;

    // === Serviços e manutenções ===
    private String limpeza;             // Ex: "Diariamente das 08h às 17h"
    private String manutencao;          // Ex: "Segunda e quinta-feira"
    private String iluminacao;          // Ex: "Postes revisados mensalmente"
    private String agua;                // Ex: "Conta de água individualizada"
    private String coletaLixo;          // Ex: "Segunda, quarta e sexta às 08h"

    // === Financeiro ===
    private String boletoLink;          // Ex: URL para segunda via
    private String formaPagamento;      // Ex: "Pix, boleto ou débito automático"
    private String taxaCondominio;      // Ex: "R$ 450,00 mensais"

    // === Reservas e agendamentos ===
    private String reservaLink;         // Ex: URL do portal de reservas
    private String politicaReservas;    // Ex: "Reservas com 3 dias de antecedência"

    // === Outras informações gerais ===
    private String estacionamentoInfo;  // Ex: "1 vaga por apartamento"
    private String visitantesInfo;      // Ex: "Permitido até 2 visitantes por unidade"
    private String animaisInfo;         // Ex: "Animais permitidos com guia nas áreas comuns"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
}

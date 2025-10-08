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
    @SequenceGenerator(name = "condominio_info_seq", sequenceName = "convivium.TB_CONDOMINIO_INFO_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "EMPRESA_ID", nullable = false, unique = true)
    private Empresa condominio;

    // Horários das áreas
    private String horarioPiscina;
    private String horarioAcademia;
    private String horarioChurrasco;
    private String horarioSalaoFestas;
    private String horarioQuadra;
    private String horarioElevador;
    private String horarioPlayground;

    // Contatos
    private String telefonePortaria;
    private String contatoSindico;

    // Regras
    private String horarioBarulho;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
}

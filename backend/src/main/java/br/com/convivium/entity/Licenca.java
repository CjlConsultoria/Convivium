package br.com.convivium.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "TB_LICENCA", schema = "convivium")
public class Licenca {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "licenca_seq")
    @SequenceGenerator(name = "licenca_seq", sequenceName = "convivium.TB_LICENCA_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @Column(name = "DATA_INICIO", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "DATA_FIM", nullable = false)
    private LocalDate dataFim;

    @Column(name = "ATIVA", nullable = false)
    private Boolean ativa;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "LIMITE_USUARIOS")
    private Integer limiteUsuarios;

    // getters e setters

    public boolean isValida() {
        LocalDate hoje = LocalDate.now();
        return ativa && (hoje.isEqual(dataInicio) || hoje.isAfter(dataInicio)) && (hoje.isBefore(dataFim) || hoje.isEqual(dataFim));
    }
}


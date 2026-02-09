package br.com.convivium.entity;

import br.com.convivium.entity.enums.StatusEncomenda;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TB_ENCOMENDA", schema = "convivium")
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encomenda_seq")
    @SequenceGenerator(
            name = "encomenda_seq",
            sequenceName = "convivium.TB_ENCOMENDA_id_seq",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    /** Código/token para o morador informar na portaria na hora da retirada (ex: 6 caracteres alfanuméricos). */
    @Column(name = "CODIGO_RETIRADA", nullable = false, unique = true, length = 20)
    private String codigoRetirada;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusEncomenda status = StatusEncomenda.AGUARDANDO;

    @Column(name = "DESCRICAO", length = 500)
    private String descricao;

    @Column(name = "DATA_RECEBIMENTO", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dataRecebimento;

    @Column(name = "DATA_RETIRADA")
    private LocalDateTime dataRetirada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MORADOR", referencedColumnName = "ID", nullable = false)
    private User morador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPRESA", referencedColumnName = "ID", nullable = false)
    private Empresa empresa;

    /** Porteiro/funcionário que registrou a encomenda. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRADO_POR", referencedColumnName = "ID")
    private User registradoPor;

    /** Porteiro/funcionário que validou a retirada (quando status = RETIRADA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RETIRADA_POR", referencedColumnName = "ID")
    private User retiradaPor;
}

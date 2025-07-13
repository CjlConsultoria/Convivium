package br.com.convivium.entity;

import br.com.convivium.entity.enums.StatusReclamacao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "TB_ACAO_RECLAMACAO", schema = "convivium")
public class AcaoReclamacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acao_reclamacao_seq")
    @SequenceGenerator(
            name = "acao_reclamacao_seq",
            sequenceName = "convivium.TB_ACAO_RECLAMACAO_id_seq",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private StatusReclamacao status;

    @Column(name = "DESCRICAO", length = 2000)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RECLAMACAO", referencedColumnName = "ID", nullable = false)
    @JsonManagedReference
    private Reclamacao reclamacao;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}

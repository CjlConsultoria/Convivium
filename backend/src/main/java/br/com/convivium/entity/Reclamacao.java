package br.com.convivium.entity;

import br.com.convivium.entity.enums.StatusReclamacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "TB_RECLAMACAO", schema = "convivium")
public class Reclamacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reclamacao_seq")
    @SequenceGenerator(
            name = "reclamacao_seq",
            sequenceName = "convivium.TB_RECLAMACAO_id_seq",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIPO", length = 100)
    private String tipo;

    @Column(name = "DETALHES", length = 2000)
    private String detalhes;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private StatusReclamacao status;

    @Column(name = "DESCRICAO_SOLUCAO", length = 2000)
    private String descricaoSolucao;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_ATUALIZACAO")
    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    @Column(name = "DATA_RESOLUCAO")
    private LocalDateTime dataResolucao;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID", nullable = false)
    private User usuario; // reclamante

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_EMPRESA", referencedColumnName = "ID", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "reclamacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Anexo> anexos = new ArrayList<>();

    @OneToMany(mappedBy = "reclamacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("reclamacao") // Ignora o campo 'reclamacao' durante a serialização
    private List<AcaoReclamacao> acoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}

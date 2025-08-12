package br.com.convivium.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "TB_RECLAMACAO_ANEXO", schema = "convivium")
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anexo_seq")
    @SequenceGenerator(
            name = "anexo_seq",
            sequenceName = "convivium.TB_RECLAMACAO_ANEXO_id_seq",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME_ARQUIVO", nullable = false)
    private String nomeArquivo;

    @Column(name = "CAMINHO_ARQUIVO", nullable = false)
    private String caminhoArquivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RECLAMACAO", referencedColumnName = "ID", nullable = false)
    @JsonBackReference
    private Reclamacao reclamacao;
}

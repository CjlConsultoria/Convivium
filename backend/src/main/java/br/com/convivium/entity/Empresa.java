package br.com.convivium.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USUARIO_EMPRESA", schema = "convivium")
@Getter
@Setter
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_seq")
    @SequenceGenerator(name = "empresa_seq", sequenceName = "convivium.TB_USUARIO_EMPRESA_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "EMPRESA", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "CNPJ", length = 18, nullable = false, unique = true)
    private String cnpj;


    @Column(name = "CEP", length = 8)
    private String cep;

    @Column(name = "LOGRADOURO", length = 100)
    private String logradouro;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 50)
    private String complemento;

    @Column(name = "BAIRRO", length = 50)
    private String bairro;

    @Column(name = "CIDADE", length = 50)
    private String cidade;

    @Column(name = "ESTADO", length = 2)
    private String estado;

    @Column(name = "CODIGO_PUBLICO", unique = true)
    private String codigoPublico;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "ID_USUARIO_RESPONSAVEL", referencedColumnName = "ID")
    private User usuarioResponsavel;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}

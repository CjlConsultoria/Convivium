package br.com.convivium.dto.request;

import br.com.convivium.entity.enums.StatusReclamacao;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReclamacaoFiltroDTO {
    private String apartamento;
    private String bloco;
    private String nomeUsuario;
    private String tipo;       // novo campo
    private String descricao;  // novo campo
    private StatusReclamacao status;     // novo campo
    private Long idEmpresa; // ✅ Novo campo

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFim;
}

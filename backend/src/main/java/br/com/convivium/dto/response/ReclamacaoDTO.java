package br.com.convivium.dto.response;

import br.com.convivium.entity.enums.StatusReclamacao;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ReclamacaoDTO {
    private Long id;
    private String tipo;
    private String detalhes;
    private StatusReclamacao status;
    private String solucao;
    private LocalDateTime dataCriacao;
    private UsuarioDTO usuario;
    private EmpresaDTO empresa;
    private List<AnexoDTO> anexos;
}


package br.com.convivium.dto.request;

import br.com.convivium.entity.enums.StatusReclamacao;
import lombok.Data;

@Data
public class AcaoReclamacaoDTO {
    private StatusReclamacao tipo;
    private String descricao;
}

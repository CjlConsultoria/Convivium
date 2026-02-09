package br.com.convivium.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EncomendaRegistroRequest {
    @NotNull(message = "ID do morador é obrigatório")
    private Long moradorId;
    @NotNull(message = "ID da empresa (condomínio) é obrigatório")
    private Long empresaId;
    private String descricao;
}

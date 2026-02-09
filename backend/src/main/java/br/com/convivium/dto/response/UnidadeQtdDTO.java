package br.com.convivium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeQtdDTO {
    private String unidade;
    private Long qtd;
}

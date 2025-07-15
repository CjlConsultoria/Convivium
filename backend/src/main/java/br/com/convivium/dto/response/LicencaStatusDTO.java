package br.com.convivium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LicencaStatusDTO {
    private boolean valida;
    private boolean expirada;
    private long diasRestantes;
    private boolean excedeuLimiteUsuarios;
}


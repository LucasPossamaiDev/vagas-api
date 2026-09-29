package com.vagas.vagas_api.dto.vaga;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarStatusVagaRequest(
    @NotBlank(message = "O status é obrigatório")
    @Pattern(regexp = "^(aberta|fechada)$", message = "O status deve ser 'aberta' ou 'fechada'")
    String status
) {
}

package com.vagas.vagas_api.dto.candidatura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarStatusCandidaturaRequest(
    @NotBlank(message = "O status é obrigatório")
    @Pattern(regexp = "^(pendente|aceita|rejeitada)$", message = "O status deve ser 'pendente', 'aceita' ou 'rejeitada'")
    String status
) {
}

package com.vagas.vagas_api.dto.candidatura;

import jakarta.validation.constraints.NotNull;

public record CriarCandidaturaRequest(
    @NotNull(message = "O ID da vaga é obrigatório")
    Long vagaId
) {
}

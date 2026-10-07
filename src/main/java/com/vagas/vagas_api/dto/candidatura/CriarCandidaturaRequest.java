package com.vagas.vagas_api.dto.candidatura;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CriarCandidaturaRequest(
    @NotNull(message = "O ID da vaga é obrigatório")
    @Positive(message = "O ID da vaga deve ser um número positivo")
    Long vagaId
) {
}

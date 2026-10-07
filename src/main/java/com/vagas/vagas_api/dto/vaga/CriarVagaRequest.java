package com.vagas.vagas_api.dto.vaga;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CriarVagaRequest(
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    String titulo,

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    String descricao,

    Set<@NotNull(message = "O ID da tecnologia não pode ser nulo") @Positive(message = "O ID da tecnologia deve ser um número positivo") Long> tecnologiaIds
) {
}

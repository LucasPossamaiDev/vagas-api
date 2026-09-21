package com.vagas.vagas_api.dto.tecnologia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarTecnologiaRequest(
    @NotBlank(message = "O nome da tecnologia é obrigatório")
    @Size(max = 150, message = "O nome da tecnologia deve ter no máximo 150 caracteres")
    String nome
) {
}

package com.vagas.vagas_api.dto.curriculo;

import jakarta.validation.constraints.Size;
import java.util.Set;

public record SalvarCurriculoRequest(
    @Size(max = 500, message = "O resumo deve ter no máximo 500 caracteres")
    String resumo,

    @Size(max = 500, message = "A URL do arquivo deve ter no máximo 500 caracteres")
    String arquivosPdfUrl,

    Set<Long> tecnologiaIds
) {
}

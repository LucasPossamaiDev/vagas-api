package com.vagas.vagas_api.dto.tecnologia;

import com.vagas.vagas_api.models.Tecnologia;

public record TecnologiaResponse(
    Long id,
    String nome
) {
    public TecnologiaResponse(Tecnologia tecnologia) {
        this(
            tecnologia.getId(),
            tecnologia.getNome()
        );
    }
}

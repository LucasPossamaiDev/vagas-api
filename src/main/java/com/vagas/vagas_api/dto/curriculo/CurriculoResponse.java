package com.vagas.vagas_api.dto.curriculo;

import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.models.Curriculo;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record CurriculoResponse(
    Long id,
    Long usuarioId,
    String resumo,
    String arquivosPdfUrl,
    Set<TecnologiaResponse> tecnologias,
    LocalDateTime atualizadoEm
) {
    public CurriculoResponse(Curriculo curriculo) {
        this(
            curriculo.getId(),
            curriculo.getUsuario().getId(),
            curriculo.getResumo(),
            curriculo.getArquivosPdfUrl(),
            curriculo.getTecnologias() != null
                ? curriculo.getTecnologias().stream().map(TecnologiaResponse::new).collect(Collectors.toSet())
                : Set.of(),
            curriculo.getAtualizadoEm()
        );
    }
}

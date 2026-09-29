package com.vagas.vagas_api.dto.vaga;

import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.models.Vaga;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record VagaResponse(
    Long id,
    EmpresaResponse empresa,
    String titulo,
    String descricao,
    String status,
    Set<TecnologiaResponse> tecnologias,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public VagaResponse(Vaga vaga) {
        this(
            vaga.getId(),
            new EmpresaResponse(vaga.getEmpresa()),
            vaga.getTitulo(),
            vaga.getDescricao(),
            vaga.getStatus(),
            vaga.getTecnologias() != null
                ? vaga.getTecnologias().stream().map(TecnologiaResponse::new).collect(Collectors.toSet())
                : Set.of(),
            vaga.getCriadoEm(),
            vaga.getAtualizadoEm()
        );
    }
}

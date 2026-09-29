package com.vagas.vagas_api.dto.candidatura;

import com.vagas.vagas_api.dto.usuario.UsuarioResponse;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.models.Candidatura;
import java.time.LocalDateTime;

public record CandidaturaResponse(
    Long id,
    VagaResponse vaga,
    UsuarioResponse usuario,
    String status,
    LocalDateTime dataCandidatura
) {
    public CandidaturaResponse(Candidatura candidatura) {
        this(
            candidatura.getId(),
            new VagaResponse(candidatura.getVaga()),
            new UsuarioResponse(candidatura.getUsuario()),
            candidatura.getStatus(),
            candidatura.getDataCandidatura()
        );
    }
}

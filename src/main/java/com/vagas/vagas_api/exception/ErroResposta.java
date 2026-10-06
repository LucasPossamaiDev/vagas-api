package com.vagas.vagas_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResposta(
        Instant timestamp,
        Integer status,
        String erro,
        String mensagem,
        String caminho,
        List<ErroCampo> erros
) {
    public ErroResposta(Integer status, String erro, String mensagem, String caminho) {
        this(Instant.now(), status, erro, mensagem, caminho, null);
    }

    public ErroResposta(Integer status, String erro, String mensagem, String caminho, List<ErroCampo> erros) {
        this(Instant.now(), status, erro, mensagem, caminho, erros);
    }
}

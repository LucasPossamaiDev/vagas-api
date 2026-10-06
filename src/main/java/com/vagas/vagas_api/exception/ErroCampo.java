package com.vagas.vagas_api.exception;

public record ErroCampo(
        String campo,
        String mensagem
) {
}

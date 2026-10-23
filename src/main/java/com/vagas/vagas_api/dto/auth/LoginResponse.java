package com.vagas.vagas_api.dto.auth;

public record LoginResponse(
        String token,
        String tipo,
        String role
) {
    public LoginResponse(String token, String role) {
        this(token, "Bearer", role);
    }
}

package com.vagas.vagas_api.config.security;

public record UserPrincipal(
        Long id,
        String email,
        String role
) {}

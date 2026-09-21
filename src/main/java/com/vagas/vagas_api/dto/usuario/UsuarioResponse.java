package com.vagas.vagas_api.dto.usuario;

import com.vagas.vagas_api.models.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nome,
    String email,
    LocalDateTime criadoEm
) {
    public UsuarioResponse(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCriadoEm()
        );
    }
}

package com.vagas.vagas_api.dto.empresa;

import com.vagas.vagas_api.models.Empresa;
import java.time.LocalDateTime;

public record EmpresaResponse(
    Long id,
    String nome,
    String email,
    String cnpj,
    LocalDateTime criadoEm
) {
    public EmpresaResponse(Empresa empresa) {
        this(
            empresa.getId(),
            empresa.getNome(),
            empresa.getEmail(),
            empresa.getCnpj(),
            empresa.getCriadoEm()
        );
    }
}

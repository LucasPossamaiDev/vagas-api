package com.vagas.vagas_api.dto.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarEmpresaRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    String nome,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
    String email,

    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
    String cnpj,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    String senha
) {
}

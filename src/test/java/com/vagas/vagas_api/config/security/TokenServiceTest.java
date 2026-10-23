package com.vagas.vagas_api.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "chave-secreta-teste-1234567890");
        ReflectionTestUtils.setField(tokenService, "expirationHours", 2);
    }

    @Test
    @DisplayName("Deve gerar token JWT com sucesso e extrair claims corretamente")
    void gerarTokenEExtrairClaims_ComSucesso() {
        Long id = 10L;
        String email = "candidato@teste.com";
        String role = "ROLE_CANDIDATO";

        String token = tokenService.gerarToken(id, email, role);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(email, tokenService.extrairEmail(token));
        assertEquals(role, tokenService.extrairRole(token));
        assertEquals(id, tokenService.extrairId(token));
    }

    @Test
    @DisplayName("Deve retornar null ao validar token inválido")
    void validarToken_Invalido_RetornaNull() {
        String tokenInvalido = "token.invalido.falso";

        assertNull(tokenService.validarETrazerJwt(tokenInvalido));
        assertNull(tokenService.extrairEmail(tokenInvalido));
        assertNull(tokenService.extrairRole(tokenInvalido));
        assertNull(tokenService.extrairId(tokenInvalido));
    }
}

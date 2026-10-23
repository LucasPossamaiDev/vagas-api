package com.vagas.vagas_api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final String ISSUER = "vagas-api";

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private Integer expirationHours;

    public String gerarToken(Long id, String email, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(email)
                    .withClaim("id", id)
                    .withClaim("role", role)
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public DecodedJWT validarETrazerJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public String extrairEmail(String token) {
        DecodedJWT decodedJWT = validarETrazerJwt(token);
        return decodedJWT != null ? decodedJWT.getSubject() : null;
    }

    public String extrairRole(String token) {
        DecodedJWT decodedJWT = validarETrazerJwt(token);
        return decodedJWT != null ? decodedJWT.getClaim("role").asString() : null;
    }

    public Long extrairId(String token) {
        DecodedJWT decodedJWT = validarETrazerJwt(token);
        return decodedJWT != null ? decodedJWT.getClaim("id").asLong() : null;
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(expirationHours).toInstant(ZoneOffset.of("-03:00"));
    }
}

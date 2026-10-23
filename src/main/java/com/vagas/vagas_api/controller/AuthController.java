package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.auth.LoginRequest;
import com.vagas.vagas_api.dto.auth.LoginResponse;
import com.vagas.vagas_api.services.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/candidato/login")
    public ResponseEntity<LoginResponse> loginCandidato(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = autenticacaoService.autenticarCandidato(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/empresa/login")
    public ResponseEntity<LoginResponse> loginEmpresa(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = autenticacaoService.autenticarEmpresa(request);
        return ResponseEntity.ok(response);
    }
}

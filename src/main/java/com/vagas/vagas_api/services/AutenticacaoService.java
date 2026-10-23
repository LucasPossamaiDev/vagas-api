package com.vagas.vagas_api.services;

import com.vagas.vagas_api.config.security.TokenService;
import com.vagas.vagas_api.dto.auth.LoginRequest;
import com.vagas.vagas_api.dto.auth.LoginResponse;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.repository.EmpresaRepository;
import com.vagas.vagas_api.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponse autenticarCandidato(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }

        String token = tokenService.gerarToken(usuario.getId(), usuario.getEmail(), "ROLE_CANDIDATO");
        return new LoginResponse(token, "ROLE_CANDIDATO");
    }

    @Transactional(readOnly = true)
    public LoginResponse autenticarEmpresa(LoginRequest request) {
        Empresa empresa = empresaRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), empresa.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }

        String token = tokenService.gerarToken(empresa.getId(), empresa.getEmail(), "ROLE_EMPRESA");
        return new LoginResponse(token, "ROLE_EMPRESA");
    }
}

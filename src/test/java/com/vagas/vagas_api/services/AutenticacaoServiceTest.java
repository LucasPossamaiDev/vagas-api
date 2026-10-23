package com.vagas.vagas_api.services;

import com.vagas.vagas_api.config.security.TokenService;
import com.vagas.vagas_api.dto.auth.LoginRequest;
import com.vagas.vagas_api.dto.auth.LoginResponse;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.repository.EmpresaRepository;
import com.vagas.vagas_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Usuario usuario;
    private Empresa empresa;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("candidato@teste.com");
        usuario.setSenha("senhaCriptografada");

        empresa = new Empresa();
        empresa.setId(2L);
        empresa.setEmail("empresa@teste.com");
        empresa.setSenha("senhaCriptografada");

        loginRequest = new LoginRequest("candidato@teste.com", "senha123");
    }

    @Test
    @DisplayName("Deve autenticar candidato com sucesso")
    void autenticarCandidato_ComSucesso() {
        when(usuarioRepository.findByEmail("candidato@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(tokenService.gerarToken(1L, "candidato@teste.com", "ROLE_CANDIDATO")).thenReturn("token-candidato");

        LoginResponse response = autenticacaoService.autenticarCandidato(loginRequest);

        assertNotNull(response);
        assertEquals("token-candidato", response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals("ROLE_CANDIDATO", response.role());
        verify(usuarioRepository, times(1)).findByEmail("candidato@teste.com");
        verify(passwordEncoder, times(1)).matches("senha123", "senhaCriptografada");
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando e-mail do candidato não for encontrado")
    void autenticarCandidato_EmailNaoEncontrado() {
        when(usuarioRepository.findByEmail("candidato@teste.com")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.autenticarCandidato(loginRequest));

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenService, never()).gerarToken(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a senha do candidato for inválida")
    void autenticarCandidato_SenhaInvalida() {
        when(usuarioRepository.findByEmail("candidato@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.autenticarCandidato(loginRequest));

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verify(tokenService, never()).gerarToken(any(), any(), any());
    }

    @Test
    @DisplayName("Deve autenticar empresa com sucesso")
    void autenticarEmpresa_ComSucesso() {
        LoginRequest empresaLoginRequest = new LoginRequest("empresa@teste.com", "senha123");
        when(empresaRepository.findByEmail("empresa@teste.com")).thenReturn(Optional.of(empresa));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        when(tokenService.gerarToken(2L, "empresa@teste.com", "ROLE_EMPRESA")).thenReturn("token-empresa");

        LoginResponse response = autenticacaoService.autenticarEmpresa(empresaLoginRequest);

        assertNotNull(response);
        assertEquals("token-empresa", response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals("ROLE_EMPRESA", response.role());
        verify(empresaRepository, times(1)).findByEmail("empresa@teste.com");
        verify(passwordEncoder, times(1)).matches("senha123", "senhaCriptografada");
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando e-mail da empresa não for encontrado")
    void autenticarEmpresa_EmailNaoEncontrado() {
        LoginRequest empresaLoginRequest = new LoginRequest("empresa@teste.com", "senha123");
        when(empresaRepository.findByEmail("empresa@teste.com")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.autenticarEmpresa(empresaLoginRequest));

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenService, never()).gerarToken(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a senha da empresa for inválida")
    void autenticarEmpresa_SenhaInvalida() {
        LoginRequest empresaLoginRequest = new LoginRequest("empresa@teste.com", "senha123");
        when(empresaRepository.findByEmail("empresa@teste.com")).thenReturn(Optional.of(empresa));
        when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.autenticarEmpresa(empresaLoginRequest));

        assertEquals("E-mail ou senha inválidos.", exception.getMessage());
        verify(tokenService, never()).gerarToken(any(), any(), any());
    }
}

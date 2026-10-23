package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.auth.LoginRequest;
import com.vagas.vagas_api.dto.auth.LoginResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.services.AutenticacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AutenticacaoService autenticacaoService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve realizar login de candidato com sucesso retornando 200 OK")
    void loginCandidato_ComSucesso() throws Exception {
        LoginResponse response = new LoginResponse("token-jwt-candidato", "ROLE_CANDIDATO");
        when(autenticacaoService.autenticarCandidato(any(LoginRequest.class))).thenReturn(response);

        String json = """
                {
                    "email": "candidato@teste.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/auth/candidato/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-candidato"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_CANDIDATO"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar login de candidato com dados inválidos")
    void loginCandidato_DadosInvalidos() throws Exception {
        String json = """
                {
                    "email": "email-invalido",
                    "senha": ""
                }
                """;

        mockMvc.perform(post("/auth/candidato/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando credenciais do candidato forem inválidas")
    void loginCandidato_CredenciaisInvalidas() throws Exception {
        when(autenticacaoService.autenticarCandidato(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("E-mail ou senha inválidos."));

        String json = """
                {
                    "email": "candidato@teste.com",
                    "senha": "senhaErrada"
                }
                """;

        mockMvc.perform(post("/auth/candidato/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detalhes").value("E-mail ou senha inválidos."));
    }

    @Test
    @DisplayName("Deve realizar login de empresa com sucesso retornando 200 OK")
    void loginEmpresa_ComSucesso() throws Exception {
        LoginResponse response = new LoginResponse("token-jwt-empresa", "ROLE_EMPRESA");
        when(autenticacaoService.autenticarEmpresa(any(LoginRequest.class))).thenReturn(response);

        String json = """
                {
                    "email": "empresa@teste.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/auth/empresa/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-empresa"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_EMPRESA"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar login de empresa com dados inválidos")
    void loginEmpresa_DadosInvalidos() throws Exception {
        String json = """
                {
                    "email": "",
                    "senha": ""
                }
                """;

        mockMvc.perform(post("/auth/empresa/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando credenciais da empresa forem inválidas")
    void loginEmpresa_CredenciaisInvalidas() throws Exception {
        when(autenticacaoService.autenticarEmpresa(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("E-mail ou senha inválidos."));

        String json = """
                {
                    "email": "empresa@teste.com",
                    "senha": "senhaErrada"
                }
                """;

        mockMvc.perform(post("/auth/empresa/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detalhes").value("E-mail ou senha inválidos."));
    }
}

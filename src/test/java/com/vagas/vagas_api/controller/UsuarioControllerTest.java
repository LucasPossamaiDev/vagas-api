package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.usuario.CriarUsuarioRequest;
import com.vagas.vagas_api.dto.usuario.UsuarioResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso retornando 201 Created")
    void cadastrar_ComSucesso() throws Exception {
        UsuarioResponse response = new UsuarioResponse(1L, "Lucas", "lucas@example.com", LocalDateTime.now());

        when(usuarioService.cadastrar(any(CriarUsuarioRequest.class))).thenReturn(response);

        String json = """
                {
                    "nome": "Lucas",
                    "email": "lucas@example.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Lucas"))
                .andExpect(jsonPath("$.email").value("lucas@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar com dados inválidos")
    void cadastrar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "nome": "",
                    "email": "email-invalido",
                    "senha": "123"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando email já existir")
    void cadastrar_QuandoEmailJaExistir_DeveRetornar400() throws Exception {
        when(usuarioService.cadastrar(any(CriarUsuarioRequest.class)))
                .thenThrow(new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail."));

        String json = """
                {
                    "nome": "Lucas",
                    "email": "lucas@example.com",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Violação de regra de negócio"))
                .andExpect(jsonPath("$.mensagem").value("Já existe um usuário cadastrado com este e-mail."));
    }

    @Test
    @DisplayName("Deve listar todos os usuários retornando 200 OK")
    void listarTodos_ComSucesso() throws Exception {
        UsuarioResponse user1 = new UsuarioResponse(1L, "Lucas", "lucas@example.com", LocalDateTime.now());
        UsuarioResponse user2 = new UsuarioResponse(2L, "Ana", "ana@example.com", LocalDateTime.now());

        when(usuarioService.listarTodos()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Lucas"))
                .andExpect(jsonPath("$[1].nome").value("Ana"));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso retornando 200 OK")
    void buscarPorId_ComSucesso() throws Exception {
        UsuarioResponse response = new UsuarioResponse(1L, "Lucas", "lucas@example.com", LocalDateTime.now());

        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Lucas"))
                .andExpect(jsonPath("$.email").value("lucas@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando usuário não for encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404() throws Exception {
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado"));
    }
}

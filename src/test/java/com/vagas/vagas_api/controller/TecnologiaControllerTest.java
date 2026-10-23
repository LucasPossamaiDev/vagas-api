package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.tecnologia.CriarTecnologiaRequest;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.services.TecnologiaService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TecnologiaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TecnologiaService tecnologiaService;

    @InjectMocks
    private TecnologiaController tecnologiaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tecnologiaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar tecnologia com sucesso retornando 201 Created")
    void cadastrar_ComSucesso() throws Exception {
        TecnologiaResponse response = new TecnologiaResponse(1L, "Java");

        when(tecnologiaService.cadastrar(any(CriarTecnologiaRequest.class))).thenReturn(response);

        String json = """
                {
                    "nome": "Java"
                }
                """;

        mockMvc.perform(post("/tecnologias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tecnologias/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Java"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar tecnologia com nome inválido")
    void cadastrar_ComNomeInvalido_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "nome": ""
                }
                """;

        mockMvc.perform(post("/tecnologias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando tecnologia já estiver cadastrada")
    void cadastrar_QuandoTecnologiaJaExiste_DeveRetornar400() throws Exception {
        when(tecnologiaService.cadastrar(any(CriarTecnologiaRequest.class)))
                .thenThrow(new RegraDeNegocioException("Tecnologia já cadastrada com esse nome."));

        String json = """
                {
                    "nome": "Java"
                }
                """;

        mockMvc.perform(post("/tecnologias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Violação de regra de negócio"))
                .andExpect(jsonPath("$.mensagem").value("Tecnologia já cadastrada com esse nome."));
    }

    @Test
    @DisplayName("Deve listar todas as tecnologias retornando 200 OK")
    void listarTodas_ComSucesso() throws Exception {
        TecnologiaResponse t1 = new TecnologiaResponse(1L, "Java");
        TecnologiaResponse t2 = new TecnologiaResponse(2L, "Spring Boot");

        when(tecnologiaService.listarTodas()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/tecnologias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Java"))
                .andExpect(jsonPath("$[1].nome").value("Spring Boot"));
    }

    @Test
    @DisplayName("Deve buscar tecnologia por ID retornando 200 OK")
    void buscarPorId_ComSucesso() throws Exception {
        TecnologiaResponse response = new TecnologiaResponse(1L, "Java");

        when(tecnologiaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/tecnologias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Java"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando tecnologia não for encontrada")
    void buscarPorId_QuandoNaoEncontrada_DeveRetornar404() throws Exception {
        when(tecnologiaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Tecnologia não encontrada"));

        mockMvc.perform(get("/tecnologias/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Tecnologia não encontrada"));
    }
}

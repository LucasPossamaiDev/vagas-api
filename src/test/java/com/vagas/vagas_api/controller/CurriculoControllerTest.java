package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.curriculo.CurriculoResponse;
import com.vagas.vagas_api.dto.curriculo.SalvarCurriculoRequest;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.services.CurriculoService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CurriculoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurriculoService curriculoService;

    @InjectMocks
    private CurriculoController curriculoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(curriculoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve salvar currículo via PUT retornando 200 OK")
    void salvarViaPut_ComSucesso() throws Exception {
        CurriculoResponse response = new CurriculoResponse(
                1L, 1L, "Desenvolvedor Backend", "http://curriculo.pdf",
                Set.of(new TecnologiaResponse(1L, "Java")), LocalDateTime.now()
        );

        when(curriculoService.salvar(eq(1L), any(SalvarCurriculoRequest.class))).thenReturn(response);

        String json = """
                {
                    "resumo": "Desenvolvedor Backend",
                    "arquivosPdfUrl": "http://curriculo.pdf",
                    "tecnologiaIds": [1]
                }
                """;

        mockMvc.perform(put("/usuarios/{usuarioId}/curriculo", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.resumo").value("Desenvolvedor Backend"));
    }

    @Test
    @DisplayName("Deve salvar currículo via POST retornando 200 OK")
    void salvarViaPost_ComSucesso() throws Exception {
        CurriculoResponse response = new CurriculoResponse(
                1L, 1L, "Desenvolvedor Backend", "http://curriculo.pdf",
                Set.of(new TecnologiaResponse(1L, "Java")), LocalDateTime.now()
        );

        when(curriculoService.salvar(eq(1L), any(SalvarCurriculoRequest.class))).thenReturn(response);

        String json = """
                {
                    "resumo": "Desenvolvedor Backend",
                    "arquivosPdfUrl": "http://curriculo.pdf",
                    "tecnologiaIds": [1]
                }
                """;

        mockMvc.perform(post("/usuarios/{usuarioId}/curriculo", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao salvar currículo com dados inválidos")
    void salvar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "resumo": "Desenvolvedor",
                    "arquivosPdfUrl": "http://curriculo.pdf",
                    "tecnologiaIds": [-5]
                }
                """;

        mockMvc.perform(put("/usuarios/{usuarioId}/curriculo", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"));
    }

    @Test
    @DisplayName("Deve buscar currículo por ID de usuário retornando 200 OK")
    void buscarPorUsuarioId_ComSucesso() throws Exception {
        CurriculoResponse response = new CurriculoResponse(
                1L, 1L, "Desenvolvedor Backend", "http://curriculo.pdf",
                Set.of(new TecnologiaResponse(1L, "Java")), LocalDateTime.now()
        );

        when(curriculoService.buscarPorUsuarioId(1L)).thenReturn(response);

        mockMvc.perform(get("/usuarios/{usuarioId}/curriculo", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando currículo não existir para o usuário")
    void buscarPorUsuarioId_QuandoNaoEncontrado_DeveRetornar404() throws Exception {
        when(curriculoService.buscarPorUsuarioId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Currículo não encontrado"));

        mockMvc.perform(get("/usuarios/{usuarioId}/curriculo", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Currículo não encontrado"));
    }

    @Test
    @DisplayName("Deve deletar currículo retornando 204 No Content")
    void deletar_ComSucesso() throws Exception {
        doNothing().when(curriculoService).deletarPorUsuarioId(1L);

        mockMvc.perform(delete("/usuarios/{usuarioId}/curriculo", 1L))
                .andExpect(status().isNoContent());

        verify(curriculoService, times(1)).deletarPorUsuarioId(1L);
    }
}

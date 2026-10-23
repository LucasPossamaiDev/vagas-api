package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.candidatura.AtualizarStatusCandidaturaRequest;
import com.vagas.vagas_api.dto.candidatura.CandidaturaResponse;
import com.vagas.vagas_api.dto.candidatura.CriarCandidaturaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.dto.usuario.UsuarioResponse;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.services.CandidaturaService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CandidaturaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CandidaturaService candidaturaService;

    @InjectMocks
    private CandidaturaController candidaturaController;

    private CandidaturaResponse candidaturaResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(candidaturaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        EmpresaResponse empresa = new EmpresaResponse(1L, "Tech Corp", "contato@tech.com", "12345678000199", LocalDateTime.now());
        VagaResponse vaga = new VagaResponse(1L, empresa, "Dev Java", "Vaga Java", "aberta", Set.of(), LocalDateTime.now(), LocalDateTime.now());
        UsuarioResponse usuario = new UsuarioResponse(1L, "Lucas", "lucas@test.com", LocalDateTime.now());

        candidaturaResponse = new CandidaturaResponse(1L, vaga, usuario, "pendente", LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar candidatura com sucesso retornando 201 Created")
    void candidatar_ComSucesso() throws Exception {
        when(candidaturaService.candidatar(eq(1L), any(CriarCandidaturaRequest.class))).thenReturn(candidaturaResponse);

        String json = """
                {
                    "vagaId": 1
                }
                """;

        mockMvc.perform(post("/usuarios/{usuarioId}/candidaturas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/candidaturas/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("pendente"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao candidatar com vagaId nulo ou inválido")
    void candidatar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "vagaId": -1
                }
                """;

        mockMvc.perform(post("/usuarios/{usuarioId}/candidaturas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando usuário já se candidatou para a vaga")
    void candidatar_QuandoCandidaturaDuplicada_DeveRetornar400() throws Exception {
        when(candidaturaService.candidatar(eq(1L), any(CriarCandidaturaRequest.class)))
                .thenThrow(new RegraDeNegocioException("Usuário já se candidatou para esta vaga."));

        String json = """
                {
                    "vagaId": 1
                }
                """;

        mockMvc.perform(post("/usuarios/{usuarioId}/candidaturas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Violação de regra de negócio"))
                .andExpect(jsonPath("$.mensagem").value("Usuário já se candidatou para esta vaga."));
    }

    @Test
    @DisplayName("Deve buscar candidatura por ID retornando 200 OK")
    void buscarPorId_ComSucesso() throws Exception {
        when(candidaturaService.buscarPorId(1L)).thenReturn(candidaturaResponse);

        mockMvc.perform(get("/candidaturas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("pendente"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando candidatura não for encontrada")
    void buscarPorId_QuandoNaoEncontrada_DeveRetornar404() throws Exception {
        when(candidaturaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Candidatura não encontrada"));

        mockMvc.perform(get("/candidaturas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Candidatura não encontrada"));
    }

    @Test
    @DisplayName("Deve listar candidaturas por usuário retornando 200 OK")
    void listarPorUsuario_ComSucesso() throws Exception {
        when(candidaturaService.listarPorUsuario(1L)).thenReturn(List.of(candidaturaResponse));

        mockMvc.perform(get("/usuarios/{usuarioId}/candidaturas", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Deve listar candidaturas por vaga retornando 200 OK")
    void listarPorVaga_ComSucesso() throws Exception {
        when(candidaturaService.listarPorVaga(1L)).thenReturn(List.of(candidaturaResponse));

        mockMvc.perform(get("/vagas/{vagaId}/candidaturas", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Deve atualizar status da candidatura retornando 200 OK")
    void atualizarStatus_ComSucesso() throws Exception {
        when(candidaturaService.atualizarStatus(eq(1L), any(AtualizarStatusCandidaturaRequest.class)))
                .thenReturn(candidaturaResponse);

        String json = """
                {
                    "status": "aceita"
                }
                """;

        mockMvc.perform(patch("/candidaturas/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar atualizar status com valor inválido")
    void atualizarStatus_ComStatusInvalido_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "status": "status_inexistente"
                }
                """;

        mockMvc.perform(patch("/candidaturas/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"));
    }

    @Test
    @DisplayName("Deve cancelar candidatura retornando 204 No Content")
    void cancelar_ComSucesso() throws Exception {
        doNothing().when(candidaturaService).cancelar(1L);

        mockMvc.perform(delete("/candidaturas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(candidaturaService, times(1)).cancelar(1L);
    }
}

package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.dto.vaga.AtualizarStatusVagaRequest;
import com.vagas.vagas_api.dto.vaga.CriarVagaRequest;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.services.VagaService;
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
class VagaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VagaService vagaService;

    @InjectMocks
    private VagaController vagaController;

    private VagaResponse vagaResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vagaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        EmpresaResponse empresa = new EmpresaResponse(1L, "Tech Corp", "contato@tech.com", "12345678000199", LocalDateTime.now());
        vagaResponse = new VagaResponse(
                1L, empresa, "Dev Java Jr", "Vaga para desenvolvedor Java", "aberta",
                Set.of(new TecnologiaResponse(1L, "Java")), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve cadastrar vaga associada a uma empresa retornando 201 Created")
    void cadastrar_ComSucesso() throws Exception {
        when(vagaService.cadastrar(eq(1L), any(CriarVagaRequest.class))).thenReturn(vagaResponse);

        String json = """
                {
                    "titulo": "Dev Java Jr",
                    "descricao": "Vaga para desenvolvedor Java",
                    "tecnologiaIds": [1]
                }
                """;

        mockMvc.perform(post("/empresas/{empresaId}/vagas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/vagas/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Dev Java Jr"))
                .andExpect(jsonPath("$.status").value("aberta"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar vaga com dados inválidos")
    void cadastrar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "titulo": "",
                    "descricao": ""
                }
                """;

        mockMvc.perform(post("/empresas/{empresaId}/vagas", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"));
    }

    @Test
    @DisplayName("Deve listar vagas de uma empresa retornando 200 OK")
    void listarPorEmpresa_ComSucesso() throws Exception {
        when(vagaService.listarPorEmpresa(1L)).thenReturn(List.of(vagaResponse));

        mockMvc.perform(get("/empresas/{empresaId}/vagas", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dev Java Jr"));
    }

    @Test
    @DisplayName("Deve listar todas as vagas com ou sem filtro de status retornando 200 OK")
    void listarTodas_ComSucesso() throws Exception {
        when(vagaService.listarTodas("aberta")).thenReturn(List.of(vagaResponse));

        mockMvc.perform(get("/vagas").param("status", "aberta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("aberta"));
    }

    @Test
    @DisplayName("Deve buscar vaga por ID retornando 200 OK")
    void buscarPorId_ComSucesso() throws Exception {
        when(vagaService.buscarPorId(1L)).thenReturn(vagaResponse);

        mockMvc.perform(get("/vagas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Dev Java Jr"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando vaga não for encontrada")
    void buscarPorId_QuandoNaoEncontrada_DeveRetornar404() throws Exception {
        when(vagaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Vaga não encontrada"));

        mockMvc.perform(get("/vagas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Vaga não encontrada"));
    }

    @Test
    @DisplayName("Deve atualizar status da vaga com sucesso retornando 200 OK")
    void atualizarStatus_ComSucesso() throws Exception {
        when(vagaService.atualizarStatus(eq(1L), any(AtualizarStatusVagaRequest.class)))
                .thenReturn(vagaResponse);

        String json = """
                {
                    "status": "fechada"
                }
                """;

        mockMvc.perform(patch("/vagas/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar atualizar status com valor inválido")
    void atualizarStatus_ComValorInvalido_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "status": "invalido"
                }
                """;

        mockMvc.perform(patch("/vagas/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"));
    }

    @Test
    @DisplayName("Deve deletar vaga retornando 204 No Content")
    void deletar_ComSucesso() throws Exception {
        doNothing().when(vagaService).deletar(1L);

        mockMvc.perform(delete("/vagas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(vagaService, times(1)).deletar(1L);
    }
}

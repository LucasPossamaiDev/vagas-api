package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.empresa.CriarEmpresaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.exception.GlobalExceptionHandler;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.services.EmpresaService;
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
class EmpresaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EmpresaController empresaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empresaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar empresa com sucesso retornando 201 Created")
    void cadastrar_ComSucesso() throws Exception {
        EmpresaResponse response = new EmpresaResponse(1L, "Tech Corp", "contato@techcorp.com", "12345678000199", LocalDateTime.now());

        when(empresaService.cadastrar(any(CriarEmpresaRequest.class))).thenReturn(response);

        String json = """
                {
                    "nome": "Tech Corp",
                    "email": "contato@techcorp.com",
                    "cnpj": "12345678000199",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/empresas/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Tech Corp"))
                .andExpect(jsonPath("$.email").value("contato@techcorp.com"))
                .andExpect(jsonPath("$.cnpj").value("12345678000199"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao cadastrar empresa com dados inválidos")
    void cadastrar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        String jsonInvalido = """
                {
                    "nome": "",
                    "email": "email-invalido",
                    "cnpj": "123",
                    "senha": "123"
                }
                """;

        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de validação"))
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao violar regra de negócio no cadastro de empresa")
    void cadastrar_QuandoCnpjJaExiste_DeveRetornar400() throws Exception {
        when(empresaService.cadastrar(any(CriarEmpresaRequest.class)))
                .thenThrow(new RegraDeNegocioException("Já existe uma empresa cadastrada com este CNPJ."));

        String json = """
                {
                    "nome": "Tech Corp",
                    "email": "contato@techcorp.com",
                    "cnpj": "12345678000199",
                    "senha": "senha123"
                }
                """;

        mockMvc.perform(post("/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Violação de regra de negócio"))
                .andExpect(jsonPath("$.mensagem").value("Já existe uma empresa cadastrada com este CNPJ."));
    }

    @Test
    @DisplayName("Deve listar todas as empresas retornando 200 OK")
    void listarTodas_ComSucesso() throws Exception {
        EmpresaResponse e1 = new EmpresaResponse(1L, "Tech Corp", "contato@techcorp.com", "12345678000199", LocalDateTime.now());
        EmpresaResponse e2 = new EmpresaResponse(2L, "Inova Inc", "contato@inova.com", "98765432000188", LocalDateTime.now());

        when(empresaService.listarTodas()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Tech Corp"))
                .andExpect(jsonPath("$[1].nome").value("Inova Inc"));
    }

    @Test
    @DisplayName("Deve buscar empresa por ID retornando 200 OK")
    void buscarPorId_ComSucesso() throws Exception {
        EmpresaResponse response = new EmpresaResponse(1L, "Tech Corp", "contato@techcorp.com", "12345678000199", LocalDateTime.now());

        when(empresaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/empresas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Tech Corp"))
                .andExpect(jsonPath("$.cnpj").value("12345678000199"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando empresa não for encontrada")
    void buscarPorId_QuandoNaoEncontrada_DeveRetornar404() throws Exception {
        when(empresaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Empresa não encontrada"));

        mockMvc.perform(get("/empresas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.mensagem").value("Empresa não encontrada"));
    }
}

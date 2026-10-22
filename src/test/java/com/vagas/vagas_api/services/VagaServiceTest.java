package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.vaga.AtualizarStatusVagaRequest;
import com.vagas.vagas_api.dto.vaga.CriarVagaRequest;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.models.Vaga;
import com.vagas.vagas_api.repository.VagaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private TecnologiaService tecnologiaService;

    @InjectMocks
    private VagaService vagaService;

    private Empresa empresa;
    private Tecnologia tecnologia;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Tech Solutions");
        empresa.setEmail("contato@techsolutions.com");
        empresa.setCnpj("12345678000199");
        empresa.setCriadoEm(LocalDateTime.now());

        tecnologia = new Tecnologia();
        tecnologia.setId(10L);
        tecnologia.setNome("Java");

        vaga = new Vaga();
        vaga.setId(100L);
        vaga.setEmpresa(empresa);
        vaga.setTitulo("Desenvolvedor Backend");
        vaga.setDescricao("Vaga para desenvolvedor Java com Spring Boot");
        vaga.setStatus("aberta");
        vaga.setTecnologias(Set.of(tecnologia));
        vaga.setCriadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar vaga com sucesso associando empresa e tecnologias")
    void cadastrar_ComSucesso() {
        CriarVagaRequest request = new CriarVagaRequest(
                "  Desenvolvedor Backend  ",
                "  Vaga para desenvolvedor Java com Spring Boot  ",
                Set.of(10L)
        );

        when(empresaService.buscarEntidadePorId(1L)).thenReturn(empresa);
        when(tecnologiaService.buscarEntidadesPorIds(Set.of(10L))).thenReturn(Set.of(tecnologia));
        when(vagaRepository.save(any(Vaga.class))).thenAnswer(invocation -> {
            Vaga v = invocation.getArgument(0);
            v.setId(100L);
            return v;
        });

        VagaResponse response = vagaService.cadastrar(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Desenvolvedor Backend", response.titulo());
        assertEquals("Vaga para desenvolvedor Java com Spring Boot", response.descricao());
        assertEquals("aberta", response.status());
        assertEquals(1, response.tecnologias().size());
        assertEquals("Tech Solutions", response.empresa().nome());

        verify(vagaRepository, times(1)).save(any(Vaga.class));
    }

    @Test
    @DisplayName("Deve buscar vaga por ID com sucesso")
    void buscarPorId_ComSucesso() {
        when(vagaRepository.findById(100L)).thenReturn(Optional.of(vaga));

        VagaResponse response = vagaService.buscarPorId(100L);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Desenvolvedor Backend", response.titulo());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar por ID inexistente")
    void buscarPorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(vagaRepository.findById(999L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> vagaService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("Vaga não encontrada com o ID: 999"));
    }

    @Test
    @DisplayName("Deve buscar entidade Vaga por ID com sucesso")
    void buscarEntidadePorId_ComSucesso() {
        when(vagaRepository.findById(100L)).thenReturn(Optional.of(vaga));

        Vaga resultado = vagaService.buscarEntidadePorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    @DisplayName("Deve listar todas as vagas quando status for nulo ou em branco")
    void listarTodas_SemStatus_DeveRetornarTodas() {
        when(vagaRepository.findAll()).thenReturn(List.of(vaga));

        List<VagaResponse> todas = vagaService.listarTodas(null);
        assertEquals(1, todas.size());

        List<VagaResponse> todasVazio = vagaService.listarTodas("   ");
        assertEquals(1, todasVazio.size());

        verify(vagaRepository, times(2)).findAll();
        verify(vagaRepository, never()).findByStatusIgnoreCase(any());
    }

    @Test
    @DisplayName("Deve filtrar vagas por status quando status for informado")
    void listarTodas_ComStatus_DeveFiltrarPorStatus() {
        when(vagaRepository.findByStatusIgnoreCase("aberta")).thenReturn(List.of(vaga));

        List<VagaResponse> resultado = vagaService.listarTodas("  aberta  ");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("aberta", resultado.get(0).status());

        verify(vagaRepository, times(1)).findByStatusIgnoreCase("aberta");
        verify(vagaRepository, never()).findAll();
    }

    @Test
    @DisplayName("Deve listar vagas por empresa com sucesso")
    void listarPorEmpresa_ComSucesso() {
        when(empresaService.buscarEntidadePorId(1L)).thenReturn(empresa);
        when(vagaRepository.findByEmpresaId(1L)).thenReturn(List.of(vaga));

        List<VagaResponse> resultado = vagaService.listarPorEmpresa(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).id());

        verify(empresaService, times(1)).buscarEntidadePorId(1L);
        verify(vagaRepository, times(1)).findByEmpresaId(1L);
    }

    @Test
    @DisplayName("Deve atualizar status da vaga com sucesso")
    void atualizarStatus_ComSucesso() {
        AtualizarStatusVagaRequest request = new AtualizarStatusVagaRequest("FECHADA");

        when(vagaRepository.findById(100L)).thenReturn(Optional.of(vaga));
        when(vagaRepository.save(any(Vaga.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VagaResponse response = vagaService.atualizarStatus(100L, request);

        assertNotNull(response);
        assertEquals("fechada", response.status());

        verify(vagaRepository, times(1)).save(vaga);
    }

    @Test
    @DisplayName("Deve deletar vaga existente com sucesso")
    void deletar_ComSucesso() {
        when(vagaRepository.findById(100L)).thenReturn(Optional.of(vaga));
        doNothing().when(vagaRepository).delete(vaga);

        assertDoesNotThrow(() -> vagaService.deletar(100L));

        verify(vagaRepository, times(1)).delete(vaga);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao deletar vaga inexistente")
    void deletar_QuandoNaoExiste_DeveLancarExcecao() {
        when(vagaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> vagaService.deletar(999L));

        verify(vagaRepository, never()).delete(any());
    }
}

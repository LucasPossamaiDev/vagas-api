package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.candidatura.AtualizarStatusCandidaturaRequest;
import com.vagas.vagas_api.dto.candidatura.CandidaturaResponse;
import com.vagas.vagas_api.dto.candidatura.CriarCandidaturaRequest;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Candidatura;
import com.vagas.vagas_api.models.Curriculo;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.models.Vaga;
import com.vagas.vagas_api.repository.CandidaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceTest {

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CurriculoService curriculoService;

    @Mock
    private VagaService vagaService;

    @InjectMocks
    private CandidaturaService candidaturaService;

    private Usuario usuario;
    private Empresa empresa;
    private Vaga vaga;
    private Curriculo curriculo;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@example.com");
        usuario.setCriadoEm(LocalDateTime.now());

        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Tech Corp");
        empresa.setEmail("tech@example.com");
        empresa.setCnpj("12.345.678/0001-90");
        empresa.setCriadoEm(LocalDateTime.now());

        vaga = new Vaga();
        vaga.setId(10L);
        vaga.setEmpresa(empresa);
        vaga.setTitulo("Desenvolvedor Backend Java");
        vaga.setDescricao("Vaga para desenvolvedor Java com Spring Boot");
        vaga.setStatus("aberta");
        vaga.setTecnologias(Collections.emptySet());
        vaga.setCriadoEm(LocalDateTime.now());

        curriculo = new Curriculo();
        curriculo.setId(1L);
        curriculo.setUsuario(usuario);
    }

    @Test
    @DisplayName("Deve realizar candidatura com sucesso quando todos os dados e regras forem válidos")
    void candidatar_ComSucesso() {
        CriarCandidaturaRequest request = new CriarCandidaturaRequest(10L);

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(curriculoService.buscarEntidadePorUsuarioId(1L)).thenReturn(curriculo);
        when(vagaService.buscarEntidadePorId(10L)).thenReturn(vaga);
        when(candidaturaRepository.existsByVagaIdAndUsuarioId(10L, 1L)).thenReturn(false);

        Candidatura candidaturaSalva = new Candidatura();
        candidaturaSalva.setId(100L);
        candidaturaSalva.setUsuario(usuario);
        candidaturaSalva.setVaga(vaga);
        candidaturaSalva.setStatus("pendente");
        candidaturaSalva.setDataCandidatura(LocalDateTime.now());

        when(candidaturaRepository.save(any(Candidatura.class))).thenReturn(candidaturaSalva);

        CandidaturaResponse response = candidaturaService.candidatar(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("pendente", response.status());
        assertEquals("Lucas", response.usuario().nome());
        assertEquals("Desenvolvedor Backend Java", response.vaga().titulo());

        verify(candidaturaRepository, times(1)).save(any(Candidatura.class));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao tentar candidatar-se a vaga fechada")
    void candidatar_QuandoVagaFechada_DeveLancarExcecao() {
        vaga.setStatus("fechada");
        CriarCandidaturaRequest request = new CriarCandidaturaRequest(10L);

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(curriculoService.buscarEntidadePorUsuarioId(1L)).thenReturn(curriculo);
        when(vagaService.buscarEntidadePorId(10L)).thenReturn(vaga);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> candidaturaService.candidatar(1L, request)
        );

        assertEquals("Não é possível se candidatar a uma vaga fechada.", exception.getMessage());
        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException quando o candidato já se inscreveu na mesma vaga")
    void candidatar_QuandoJaCandidatou_DeveLancarExcecao() {
        CriarCandidaturaRequest request = new CriarCandidaturaRequest(10L);

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(curriculoService.buscarEntidadePorUsuarioId(1L)).thenReturn(curriculo);
        when(vagaService.buscarEntidadePorId(10L)).thenReturn(vaga);
        when(candidaturaRepository.existsByVagaIdAndUsuarioId(10L, 1L)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> candidaturaService.candidatar(1L, request)
        );

        assertEquals("O usuário já se candidatou a esta vaga.", exception.getMessage());
        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar candidatura por ID com sucesso")
    void buscarPorId_ComSucesso() {
        Candidatura candidatura = new Candidatura();
        candidatura.setId(100L);
        candidatura.setUsuario(usuario);
        candidatura.setVaga(vaga);
        candidatura.setStatus("pendente");
        candidatura.setDataCandidatura(LocalDateTime.now());

        when(candidaturaRepository.findById(100L)).thenReturn(Optional.of(candidatura));

        CandidaturaResponse response = candidaturaService.buscarPorId(100L);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("pendente", response.status());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando ID não existir")
    void buscarPorId_NaoEncontrado_DeveLancarExcecao() {
        when(candidaturaRepository.findById(999L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> candidaturaService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("Candidatura não encontrada com o ID: 999"));
    }

    @Test
    @DisplayName("Deve atualizar status da candidatura com sucesso")
    void atualizarStatus_ComSucesso() {
        Candidatura candidatura = new Candidatura();
        candidatura.setId(100L);
        candidatura.setUsuario(usuario);
        candidatura.setVaga(vaga);
        candidatura.setStatus("pendente");
        candidatura.setDataCandidatura(LocalDateTime.now());

        when(candidaturaRepository.findById(100L)).thenReturn(Optional.of(candidatura));
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarStatusCandidaturaRequest request = new AtualizarStatusCandidaturaRequest("aceita");
        CandidaturaResponse response = candidaturaService.atualizarStatus(100L, request);

        assertNotNull(response);
        assertEquals("aceita", response.status());
        verify(candidaturaRepository, times(1)).save(candidatura);
    }

    @Test
    @DisplayName("Deve cancelar candidatura existente com sucesso")
    void cancelar_ComSucesso() {
        Candidatura candidatura = new Candidatura();
        candidatura.setId(100L);

        when(candidaturaRepository.findById(100L)).thenReturn(Optional.of(candidatura));
        doNothing().when(candidaturaRepository).delete(candidatura);

        assertDoesNotThrow(() -> candidaturaService.cancelar(100L));
        verify(candidaturaRepository, times(1)).delete(candidatura);
    }
}

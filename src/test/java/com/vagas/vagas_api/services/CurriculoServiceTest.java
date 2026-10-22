package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.curriculo.CurriculoResponse;
import com.vagas.vagas_api.dto.curriculo.SalvarCurriculoRequest;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.models.Curriculo;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.repository.CurriculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculoServiceTest {

    @Mock
    private CurriculoRepository curriculoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private TecnologiaService tecnologiaService;

    @InjectMocks
    private CurriculoService curriculoService;

    private Usuario usuario;
    private Curriculo curriculo;
    private Tecnologia tecnologia;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@example.com");

        tecnologia = new Tecnologia();
        tecnologia.setId(10L);
        tecnologia.setNome("Java");

        curriculo = new Curriculo();
        curriculo.setId(1L);
        curriculo.setUsuario(usuario);
        curriculo.setResumo("Desenvolvedor Java Backend");
        curriculo.setArquivosPdfUrl("https://storage.example.com/cv.pdf");
        curriculo.setTecnologias(Set.of(tecnologia));
        curriculo.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar novo currículo quando usuário não possuir currículo cadastrado")
    void salvar_QuandoCurriculoNovo_DeveCriarComSucesso() {
        SalvarCurriculoRequest request = new SalvarCurriculoRequest(
                "Desenvolvedor Java Backend",
                "https://storage.example.com/cv.pdf",
                Set.of(10L)
        );

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(curriculoRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(tecnologiaService.buscarEntidadesPorIds(Set.of(10L))).thenReturn(Set.of(tecnologia));
        when(curriculoRepository.save(any(Curriculo.class))).thenAnswer(invocation -> {
            Curriculo c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CurriculoResponse response = curriculoService.salvar(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.usuarioId());
        assertEquals("Desenvolvedor Java Backend", response.resumo());
        assertEquals("https://storage.example.com/cv.pdf", response.arquivosPdfUrl());
        assertEquals(1, response.tecnologias().size());

        verify(curriculoRepository, times(1)).save(any(Curriculo.class));
    }

    @Test
    @DisplayName("Deve atualizar currículo existente quando usuário já possuir currículo cadastrado")
    void salvar_QuandoCurriculoExistente_DeveAtualizarComSucesso() {
        SalvarCurriculoRequest request = new SalvarCurriculoRequest(
                "Resumo Atualizado",
                "https://storage.example.com/novo_cv.pdf",
                Set.of(10L)
        );

        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(curriculoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(curriculo));
        when(tecnologiaService.buscarEntidadesPorIds(Set.of(10L))).thenReturn(Set.of(tecnologia));
        when(curriculoRepository.save(any(Curriculo.class))).thenReturn(curriculo);

        CurriculoResponse response = curriculoService.salvar(1L, request);

        assertNotNull(response);
        assertEquals("Resumo Atualizado", response.resumo());
        assertEquals("https://storage.example.com/novo_cv.pdf", response.arquivosPdfUrl());

        verify(curriculoRepository, times(1)).save(curriculo);
    }

    @Test
    @DisplayName("Deve buscar currículo por ID de usuário com sucesso")
    void buscarPorUsuarioId_ComSucesso() {
        when(curriculoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(curriculo));

        CurriculoResponse response = curriculoService.buscarPorUsuarioId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.usuarioId());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar currículo de usuário inexistente")
    void buscarPorUsuarioId_QuandoNaoExiste_DeveLancarExcecao() {
        when(curriculoRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> curriculoService.buscarPorUsuarioId(99L)
        );

        assertTrue(exception.getMessage().contains("Currículo não encontrado para o usuário com ID: 99"));
    }

    @Test
    @DisplayName("Deve buscar entidade Curriculo por ID com sucesso")
    void buscarEntidadePorId_ComSucesso() {
        when(curriculoRepository.findById(1L)).thenReturn(Optional.of(curriculo));

        Curriculo resultado = curriculoService.buscarEntidadePorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar entidade Curriculo por ID inexistente")
    void buscarEntidadePorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(curriculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> curriculoService.buscarEntidadePorId(99L));
    }

    @Test
    @DisplayName("Deve buscar entidade Curriculo por ID do usuário com sucesso")
    void buscarEntidadePorUsuarioId_ComSucesso() {
        when(curriculoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(curriculo));

        Curriculo resultado = curriculoService.buscarEntidadePorUsuarioId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar entidade por usuário inexistente")
    void buscarEntidadePorUsuarioId_QuandoNaoExiste_DeveLancarExcecao() {
        when(curriculoRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> curriculoService.buscarEntidadePorUsuarioId(99L));
    }

    @Test
    @DisplayName("Deve deletar currículo do usuário com sucesso")
    void deletarPorUsuarioId_ComSucesso() {
        when(curriculoRepository.findByUsuarioId(1L)).thenReturn(Optional.of(curriculo));
        doNothing().when(curriculoRepository).delete(curriculo);

        assertDoesNotThrow(() -> curriculoService.deletarPorUsuarioId(1L));
        verify(curriculoRepository, times(1)).delete(curriculo);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar deletar currículo de usuário inexistente")
    void deletarPorUsuarioId_QuandoNaoExiste_DeveLancarExcecao() {
        when(curriculoRepository.findByUsuarioId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> curriculoService.deletarPorUsuarioId(99L));
        verify(curriculoRepository, never()).delete(any());
    }
}

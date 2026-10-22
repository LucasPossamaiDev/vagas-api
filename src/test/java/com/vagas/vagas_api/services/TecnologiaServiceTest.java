package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.tecnologia.CriarTecnologiaRequest;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.repository.TecnologiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TecnologiaServiceTest {

    @Mock
    private TecnologiaRepository tecnologiaRepository;

    @InjectMocks
    private TecnologiaService tecnologiaService;

    private Tecnologia tecnologia;

    @BeforeEach
    void setUp() {
        tecnologia = new Tecnologia();
        tecnologia.setId(1L);
        tecnologia.setNome("Java");
    }

    @Test
    @DisplayName("Deve cadastrar tecnologia com sucesso removendo espaços em branco extras")
    void cadastrar_ComSucesso() {
        CriarTecnologiaRequest request = new CriarTecnologiaRequest("  Java  ");

        when(tecnologiaRepository.existsByNomeIgnoreCase("Java")).thenReturn(false);
        when(tecnologiaRepository.save(any(Tecnologia.class))).thenReturn(tecnologia);

        TecnologiaResponse response = tecnologiaService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Java", response.nome());

        verify(tecnologiaRepository, times(1)).save(any(Tecnologia.class));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException quando o nome da tecnologia já existir")
    void cadastrar_QuandoNomeJaExiste_DeveLancarExcecao() {
        CriarTecnologiaRequest request = new CriarTecnologiaRequest("Java");

        when(tecnologiaRepository.existsByNomeIgnoreCase("Java")).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> tecnologiaService.cadastrar(request)
        );

        assertEquals("Já existe uma tecnologia cadastrada com o nome: Java", exception.getMessage());
        verify(tecnologiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar tecnologia por ID com sucesso")
    void buscarPorId_ComSucesso() {
        when(tecnologiaRepository.findById(1L)).thenReturn(Optional.of(tecnologia));

        TecnologiaResponse response = tecnologiaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Java", response.nome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando ID não existir")
    void buscarPorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(tecnologiaRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> tecnologiaService.buscarPorId(99L)
        );

        assertTrue(exception.getMessage().contains("Tecnologia não encontrada com ID: 99"));
    }

    @Test
    @DisplayName("Deve listar todas as tecnologias")
    void listarTodas_ComSucesso() {
        when(tecnologiaRepository.findAll()).thenReturn(List.of(tecnologia));

        List<TecnologiaResponse> lista = tecnologiaService.listarTodas();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Java", lista.get(0).nome());
    }

    @Test
    @DisplayName("Deve buscar entidade Tecnologia por ID com sucesso")
    void buscarEntidadePorId_ComSucesso() {
        when(tecnologiaRepository.findById(1L)).thenReturn(Optional.of(tecnologia));

        Tecnologia resultado = tecnologiaService.buscarEntidadePorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Java", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar entidade inexistente")
    void buscarEntidadePorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(tecnologiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> tecnologiaService.buscarEntidadePorId(99L));
    }

    @Test
    @DisplayName("Deve retornar Set vazio quando IDs forem nulos ou vazios")
    void buscarEntidadesPorIds_QuandoIdsNullOuVazio_DeveRetornarVazio() {
        Set<Tecnologia> resultadoNull = tecnologiaService.buscarEntidadesPorIds(null);
        assertNotNull(resultadoNull);
        assertTrue(resultadoNull.isEmpty());

        Set<Tecnologia> resultadoVazio = tecnologiaService.buscarEntidadesPorIds(Collections.emptySet());
        assertNotNull(resultadoVazio);
        assertTrue(resultadoVazio.isEmpty());

        verify(tecnologiaRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("Deve retornar Set de tecnologias quando todos os IDs existirem")
    void buscarEntidadesPorIds_QuandoTodosExistem_DeveRetornarSet() {
        Tecnologia tec2 = new Tecnologia();
        tec2.setId(2L);
        tec2.setNome("Spring Boot");

        Set<Long> ids = Set.of(1L, 2L);
        when(tecnologiaRepository.findAllById(ids)).thenReturn(List.of(tecnologia, tec2));

        Set<Tecnologia> resultado = tecnologiaService.buscarEntidadesPorIds(ids);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando alguma tecnologia não for encontrada")
    void buscarEntidadesPorIds_QuandoAlgumIdNaoExiste_DeveLancarExcecao() {
        Set<Long> ids = Set.of(1L, 2L);
        when(tecnologiaRepository.findAllById(ids)).thenReturn(List.of(tecnologia));

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> tecnologiaService.buscarEntidadesPorIds(ids)
        );

        assertEquals("Uma ou mais tecnologias informadas não foram encontradas.", exception.getMessage());
    }
}

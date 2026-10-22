package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.empresa.CriarEmpresaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.repository.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Tech Solutions");
        empresa.setEmail("contato@techsolutions.com");
        empresa.setCnpj("12345678000199");
        empresa.setSenha("encodedPassword123");
        empresa.setCriadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar empresa com sucesso e criptografar a senha")
    void cadastrar_ComSucesso() {
        CriarEmpresaRequest request = new CriarEmpresaRequest(
                "Tech Solutions",
                "contato@techsolutions.com",
                "12345678000199",
                "senhaForte123"
        );

        when(empresaRepository.existsByEmail("contato@techsolutions.com")).thenReturn(false);
        when(empresaRepository.existsByCnpj("12345678000199")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("encodedPassword123");
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaResponse response = empresaService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Tech Solutions", response.nome());
        assertEquals("contato@techsolutions.com", response.email());
        assertEquals("12345678000199", response.cnpj());

        verify(passwordEncoder, times(1)).encode("senhaForte123");
        verify(empresaRepository, times(1)).save(any(Empresa.class));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException quando e-mail já estiver cadastrado")
    void cadastrar_QuandoEmailJaCadastrado_DeveLancarExcecao() {
        CriarEmpresaRequest request = new CriarEmpresaRequest(
                "Tech Solutions",
                "contato@techsolutions.com",
                "12345678000199",
                "senhaForte123"
        );

        when(empresaRepository.existsByEmail("contato@techsolutions.com")).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> empresaService.cadastrar(request)
        );

        assertEquals("Já existe uma empresa cadastrada com este e-mail.", exception.getMessage());
        verify(empresaRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException quando CNPJ já estiver cadastrado")
    void cadastrar_QuandoCnpjJaCadastrado_DeveLancarExcecao() {
        CriarEmpresaRequest request = new CriarEmpresaRequest(
                "Tech Solutions",
                "contato@techsolutions.com",
                "12345678000199",
                "senhaForte123"
        );

        when(empresaRepository.existsByEmail("contato@techsolutions.com")).thenReturn(false);
        when(empresaRepository.existsByCnpj("12345678000199")).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> empresaService.cadastrar(request)
        );

        assertEquals("Já existe uma empresa cadastrada com este CNPJ.", exception.getMessage());
        verify(empresaRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve buscar empresa por ID com sucesso")
    void buscarPorId_ComSucesso() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        EmpresaResponse response = empresaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Tech Solutions", response.nome());
        assertEquals("contato@techsolutions.com", response.email());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar por ID inexistente")
    void buscarPorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> empresaService.buscarPorId(99L)
        );

        assertTrue(exception.getMessage().contains("Empresa não encontrada com ID: 99"));
    }

    @Test
    @DisplayName("Deve listar todas as empresas")
    void listarTodas_ComSucesso() {
        when(empresaRepository.findAll()).thenReturn(List.of(empresa));

        List<EmpresaResponse> lista = empresaService.listarTodas();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Tech Solutions", lista.get(0).nome());
    }

    @Test
    @DisplayName("Deve buscar entidade Empresa por ID com sucesso")
    void buscarEntidadePorId_ComSucesso() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        Empresa resultado = empresaService.buscarEntidadePorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tech Solutions", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar entidade com ID inexistente")
    void buscarEntidadePorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> empresaService.buscarEntidadePorId(99L));
    }
}

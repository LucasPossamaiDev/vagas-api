package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.usuario.CriarUsuarioRequest;
import com.vagas.vagas_api.dto.usuario.UsuarioResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@example.com");
        usuario.setSenha("senhaCodificada123");
        usuario.setCriadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso e codificar a senha")
    void cadastrar_ComSucesso() {
        CriarUsuarioRequest request = new CriarUsuarioRequest("Lucas", "lucas@example.com", "senha123");

        when(usuarioRepository.existsByEmail("lucas@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Lucas", response.nome());
        assertEquals("lucas@example.com", response.email());

        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao tentar cadastrar e-mail já existente")
    void cadastrar_QuandoEmailJaExiste_DeveLancarExcecao() {
        CriarUsuarioRequest request = new CriarUsuarioRequest("Lucas", "lucas@example.com", "senha123");

        when(usuarioRepository.existsByEmail("lucas@example.com")).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals("Já existe um usuário cadastrado com este e-mail.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void buscarPorId_ComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Lucas", response.nome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não existir")
    void buscarPorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarTodos_ComSucesso() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponse> lista = usuarioService.listarTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("lucas@example.com", lista.get(0).email());
    }
}

package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.curriculo.CurriculoResponse;
import com.vagas.vagas_api.dto.curriculo.SalvarCurriculoRequest;
import com.vagas.vagas_api.dto.empresa.CriarEmpresaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Curriculo;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.repository.CurriculoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class CurriculoService {

    private final CurriculoRepository curriculoRepository;
    private final UsuarioService usuarioService;
    private final TecnologiaService tecnologiaService;

    public CurriculoService(
            CurriculoRepository curriculoRepository,
            UsuarioService usuarioService,
            TecnologiaService tecnologiaService
    ) {
        this.curriculoRepository = curriculoRepository;
        this.usuarioService = usuarioService;
        this.tecnologiaService = tecnologiaService;
    }

    //Salva ou atualiza o currículo do usuário (comportamento upsert).
    @Transactional
    public CurriculoResponse salvar(Long usuarioId, SalvarCurriculoRequest request) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);

        // Busca se o usuário já possui currículo ou cria uma nova instância
        Curriculo curriculo = curriculoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Curriculo novoCurriculo = new Curriculo();
                    novoCurriculo.setUsuario(usuario);
                    return novoCurriculo;
                });

        // Carrega o Set de tecnologias validadas pelo TecnologiaService
        Set<Tecnologia> tecnologias = tecnologiaService.buscarEntidadesPorIds(request.tecnologiaIds());

        curriculo.setResumo(request.resumo());
        curriculo.setArquivosPdfUrl(request.arquivosPdfUrl());
        curriculo.setTecnologias(tecnologias);

        Curriculo curriculoSalvo = curriculoRepository.save(curriculo);
        return new CurriculoResponse(curriculoSalvo);
    }

    // Busca o currículo pelo ID do usuário.
    @Transactional(readOnly = true)
    public CurriculoResponse buscarPorUsuarioId(Long usuarioId) {
        Curriculo curriculo = curriculoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Currículo não encontrado para o usuário com ID: " + usuarioId
                ));
        return new CurriculoResponse(curriculo);
    }

     //Busca a entidade Curriculo pelo ID do currículo.
    @Transactional(readOnly = true)
    public Curriculo buscarEntidadePorId(Long id) {
        return curriculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Currículo não encontrado com o ID: " + id
                ));
    }

     //Busca a entidade Curriculo pelo ID do usuário (útil para o CandidaturaService).
    @Transactional(readOnly = true)
    public Curriculo buscarEntidadePorUsuarioId(Long usuarioId) {
        return curriculoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Currículo não encontrado para o usuário com ID: " + usuarioId
                ));
    }

     //Deleta o currículo vinculado ao usuário.
    @Transactional
    public void deletarPorUsuarioId(Long usuarioId) {
        Curriculo curriculo = buscarEntidadePorUsuarioId(usuarioId);
        curriculoRepository.delete(curriculo);
    }
}

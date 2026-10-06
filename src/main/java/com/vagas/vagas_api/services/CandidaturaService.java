package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.candidatura.AtualizarStatusCandidaturaRequest;
import com.vagas.vagas_api.dto.candidatura.CandidaturaResponse;
import com.vagas.vagas_api.dto.candidatura.CriarCandidaturaRequest;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Candidatura;
import com.vagas.vagas_api.models.Usuario;
import com.vagas.vagas_api.models.Vaga;
import com.vagas.vagas_api.repository.CandidaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final UsuarioService usuarioService;
    private final CurriculoService curriculoService;
    private final VagaService vagaService;

    public CandidaturaService(
            CandidaturaRepository candidaturaRepository,
            UsuarioService usuarioService,
            CurriculoService curriculoService,
            VagaService vagaService
    ) {
        this.candidaturaRepository = candidaturaRepository;
        this.usuarioService = usuarioService;
        this.curriculoService = curriculoService;
        this.vagaService = vagaService;
    }

    @Transactional
    public CandidaturaResponse candidatar(Long usuarioId, CriarCandidaturaRequest request) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);

        // Valida se o candidato possui currículo cadastrado
        curriculoService.buscarEntidadePorUsuarioId(usuarioId);

        Vaga vaga = vagaService.buscarEntidadePorId(request.vagaId());

        if (!"aberta".equalsIgnoreCase(vaga.getStatus())) {
            throw new RegraDeNegocioException("Não é possível se candidatar a uma vaga fechada.");
        }

        if (candidaturaRepository.existsByVagaIdAndUsuarioId(request.vagaId(), usuarioId)) {
            throw new RegraDeNegocioException("O usuário já se candidatou a esta vaga.");
        }

        Candidatura candidatura = new Candidatura();
        candidatura.setUsuario(usuario);
        candidatura.setVaga(vaga);
        candidatura.setStatus("pendente");

        Candidatura candidaturaSalva = candidaturaRepository.save(candidatura);
        return new CandidaturaResponse(candidaturaSalva);
    }

    @Transactional(readOnly = true)
    public CandidaturaResponse buscarPorId(Long id) {
        Candidatura candidatura = buscarEntidadePorId(id);
        return new CandidaturaResponse(candidatura);
    }

    @Transactional(readOnly = true)
    public Candidatura buscarEntidadePorId(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidatura não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<CandidaturaResponse> listarPorUsuario(Long usuarioId) {
        usuarioService.buscarEntidadePorId(usuarioId);
        return candidaturaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(CandidaturaResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidaturaResponse> listarPorVaga(Long vagaId) {
        vagaService.buscarEntidadePorId(vagaId);
        return candidaturaRepository.findByVagaId(vagaId)
                .stream()
                .map(CandidaturaResponse::new)
                .toList();
    }

    @Transactional
    public CandidaturaResponse atualizarStatus(Long id, AtualizarStatusCandidaturaRequest request) {
        Candidatura candidatura = buscarEntidadePorId(id);
        candidatura.setStatus(request.status().trim().toLowerCase());
        Candidatura atualizada = candidaturaRepository.save(candidatura);
        return new CandidaturaResponse(atualizada);
    }

    @Transactional
    public void cancelar(Long id) {
        Candidatura candidatura = buscarEntidadePorId(id);
        candidaturaRepository.delete(candidatura);
    }
}

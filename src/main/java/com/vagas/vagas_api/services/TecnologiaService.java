package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.tecnologia.CriarTecnologiaRequest;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.repository.TecnologiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TecnologiaService {

    private final TecnologiaRepository tecnologiaRepository;

    public TecnologiaService(TecnologiaRepository tecnologiaRepository) {
        this.tecnologiaRepository = tecnologiaRepository;
    }

    @Transactional
    public TecnologiaResponse cadastrar(CriarTecnologiaRequest request) {
        String nome = request.nome().trim();

        if (tecnologiaRepository.existsByNomeIgnoreCase(nome)) {
            throw new RegraDeNegocioException("Já existe uma tecnologia cadastrada com o nome: " + nome);
        }

        Tecnologia tecnologia = new Tecnologia();
        tecnologia.setNome(nome);

        Tecnologia tecnologiaSalva = tecnologiaRepository.save(tecnologia);
        return new TecnologiaResponse(tecnologiaSalva);
    }

    @Transactional(readOnly = true)
    public TecnologiaResponse buscarPorId(Long id) {
        Tecnologia tecnologia = buscarEntidadePorId(id);
        return new TecnologiaResponse(tecnologia);
    }

    @Transactional(readOnly = true)
    public List<TecnologiaResponse> listarTodas() {
        return tecnologiaRepository.findAll()
                .stream()
                .map(TecnologiaResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Tecnologia buscarEntidadePorId(Long id) {
        return tecnologiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tecnologia não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Set<Tecnologia> buscarEntidadesPorIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }

        List<Tecnologia> tecnologias = tecnologiaRepository.findAllById(ids);
        if (tecnologias.size() != ids.size()) {
            throw new RecursoNaoEncontradoException("Uma ou mais tecnologias informadas não foram encontradas.");
        }

        return new HashSet<>(tecnologias);
    }
}

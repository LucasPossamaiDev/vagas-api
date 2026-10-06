package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.vaga.AtualizarStatusVagaRequest;
import com.vagas.vagas_api.dto.vaga.CriarVagaRequest;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.models.Tecnologia;
import com.vagas.vagas_api.models.Vaga;
import com.vagas.vagas_api.repository.VagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final EmpresaService empresaService;
    private final TecnologiaService tecnologiaService;

    public VagaService(
            VagaRepository vagaRepository,
            EmpresaService empresaService,
            TecnologiaService tecnologiaService
    ) {
        this.vagaRepository = vagaRepository;
        this.empresaService = empresaService;
        this.tecnologiaService = tecnologiaService;
    }

    // Cadastra uma nova vaga associada a uma empresa e tecnologias.
    @Transactional
    public VagaResponse cadastrar(Long empresaId, CriarVagaRequest request) {
        Empresa empresa = empresaService.buscarEntidadePorId(empresaId);
        Set<Tecnologia> tecnologias = tecnologiaService.buscarEntidadesPorIds(request.tecnologiaIds());

        Vaga vaga = new Vaga();
        vaga.setEmpresa(empresa);
        vaga.setTitulo(request.titulo().trim());
        vaga.setDescricao(request.descricao().trim());
        vaga.setStatus("aberta");
        vaga.setTecnologias(tecnologias);

        Vaga vagaSalva = vagaRepository.save(vaga);
        return new VagaResponse(vagaSalva);
    }

    // Busca uma vaga pelo ID e retorna a resposta correspondente.
    @Transactional(readOnly = true)
    public VagaResponse buscarPorId(Long id) {
        Vaga vaga = buscarEntidadePorId(id);
        return new VagaResponse(vaga);
    }

    // Busca a entidade Vaga pelo ID, lançando uma exceção se não encontrada.
    @Transactional(readOnly = true)
    public Vaga buscarEntidadePorId(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada com o ID: " + id));
    }

    // Lista todas as vagas, podendo filtrar por status se fornecido.
    @Transactional(readOnly = true)
    public List<VagaResponse> listarTodas(String status) {
        List<Vaga> vagas;
        if (status != null && !status.isBlank()) {
            vagas = vagaRepository.findByStatusIgnoreCase(status.trim());
        } else {
            vagas = vagaRepository.findAll();
        }
        return vagas.stream()
                .map(VagaResponse::new)
                .toList();
    }

    // Lista todas as vagas de uma empresa específica.
    @Transactional(readOnly = true)
    public List<VagaResponse> listarPorEmpresa(Long empresaId) {
        empresaService.buscarEntidadePorId(empresaId);
        return vagaRepository.findByEmpresaId(empresaId)
                .stream()
                .map(VagaResponse::new)
                .toList();
    }

    // Atualiza o status de uma vaga específica.
    @Transactional
    public VagaResponse atualizarStatus(Long id, AtualizarStatusVagaRequest request) {
        Vaga vaga = buscarEntidadePorId(id);
        vaga.setStatus(request.status().trim().toLowerCase());
        Vaga vagaAtualizada = vagaRepository.save(vaga);
        return new VagaResponse(vagaAtualizada);
    }

    // Deleta uma vaga pelo ID, lançando uma exceção se não encontrada.
    @Transactional
    public void deletar(Long id) {
        Vaga vaga = buscarEntidadePorId(id);
        vagaRepository.delete(vaga);
    }
}

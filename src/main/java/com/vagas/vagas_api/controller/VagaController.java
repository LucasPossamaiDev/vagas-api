package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.vaga.AtualizarStatusVagaRequest;
import com.vagas.vagas_api.dto.vaga.CriarVagaRequest;
import com.vagas.vagas_api.dto.vaga.VagaResponse;
import com.vagas.vagas_api.services.VagaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    // Endpoint para cadastrar uma nova vaga associada a uma empresa
    @PostMapping("/empresas/{empresaId}/vagas")
    public ResponseEntity<VagaResponse> cadastrar(
            @PathVariable Long empresaId,
            @RequestBody @Valid CriarVagaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        VagaResponse response = vagaService.cadastrar(empresaId, request);
        URI uri = uriBuilder.path("/vagas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Endpoint para listar todas as vagas de uma empresa específica
    @GetMapping("/empresas/{empresaId}/vagas")
    public ResponseEntity<List<VagaResponse>> listarPorEmpresa(@PathVariable Long empresaId) {
        List<VagaResponse> vagas = vagaService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(vagas);
    }

    // Endpoint para listar todas as vagas, com filtro opcional por status
    @GetMapping("/vagas")
    public ResponseEntity<List<VagaResponse>> listarTodas(
            @RequestParam(required = false) String status
    ) {
        List<VagaResponse> vagas = vagaService.listarTodas(status);
        return ResponseEntity.ok(vagas);
    }

    // Endpoint para buscar uma vaga por ID
    @GetMapping("/vagas/{id}")
    public ResponseEntity<VagaResponse> buscarPorId(@PathVariable Long id) {
        VagaResponse response = vagaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Endpoint para atualizar o status de uma vaga
    @PatchMapping("/vagas/{id}/status")
    public ResponseEntity<VagaResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusVagaRequest request
    ) {
        VagaResponse response = vagaService.atualizarStatus(id, request);
        return ResponseEntity.ok(response);
    }

    // Endpoint para deletar uma vaga por ID
    @DeleteMapping("/vagas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vagaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

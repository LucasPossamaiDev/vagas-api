package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.candidatura.AtualizarStatusCandidaturaRequest;
import com.vagas.vagas_api.dto.candidatura.CandidaturaResponse;
import com.vagas.vagas_api.dto.candidatura.CriarCandidaturaRequest;
import com.vagas.vagas_api.services.CandidaturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    // Endpoint para realizar candidatura de um usuário em uma vaga
    @PostMapping("/usuarios/{usuarioId}/candidaturas")
    public ResponseEntity<CandidaturaResponse> candidatar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid CriarCandidaturaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        CandidaturaResponse response = candidaturaService.candidatar(usuarioId, request);
        URI uri = uriBuilder.path("/candidaturas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Endpoint para buscar uma candidatura por ID
    @GetMapping("/candidaturas/{id}")
    public ResponseEntity<CandidaturaResponse> buscarPorId(@PathVariable Long id) {
        CandidaturaResponse response = candidaturaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Endpoint para listar candidaturas de um usuário
    @GetMapping("/usuarios/{usuarioId}/candidaturas")
    public ResponseEntity<List<CandidaturaResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<CandidaturaResponse> candidaturas = candidaturaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(candidaturas);
    }

    // Endpoint para listar candidaturas de uma vaga específica
    @GetMapping("/vagas/{vagaId}/candidaturas")
    public ResponseEntity<List<CandidaturaResponse>> listarPorVaga(@PathVariable Long vagaId) {
        List<CandidaturaResponse> candidaturas = candidaturaService.listarPorVaga(vagaId);
        return ResponseEntity.ok(candidaturas);
    }

    // Endpoint para atualizar o status de uma candidatura
    @PatchMapping("/candidaturas/{id}/status")
    public ResponseEntity<CandidaturaResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusCandidaturaRequest request
    ) {
        CandidaturaResponse response = candidaturaService.atualizarStatus(id, request);
        return ResponseEntity.ok(response);
    }

    // Endpoint para cancelar uma candidatura
    @DeleteMapping("/candidaturas/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        candidaturaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}

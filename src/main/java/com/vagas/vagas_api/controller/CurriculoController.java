package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.curriculo.CurriculoResponse;
import com.vagas.vagas_api.dto.curriculo.SalvarCurriculoRequest;
import com.vagas.vagas_api.services.CurriculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/{usuarioId}/curriculo")
public class CurriculoController {

    private final CurriculoService curriculoService;

    public CurriculoController(CurriculoService curriculoService) {
        this.curriculoService = curriculoService;
    }

    @PutMapping
    public ResponseEntity<CurriculoResponse> salvar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid SalvarCurriculoRequest request
    ) {
        CurriculoResponse response = curriculoService.salvar(usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CurriculoResponse> criarOuAtualizar(
            @PathVariable Long usuarioId,
            @RequestBody @Valid SalvarCurriculoRequest request
    ) {
        CurriculoResponse response = curriculoService.salvar(usuarioId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CurriculoResponse> buscarPorUsuarioId(@PathVariable Long usuarioId) {
        CurriculoResponse response = curriculoService.buscarPorUsuarioId(usuarioId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(@PathVariable Long usuarioId) {
        curriculoService.deletarPorUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

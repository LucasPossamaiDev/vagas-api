package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.empresa.CriarEmpresaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Endpoint para cadastrar uma nova empresa.
    @PostMapping
    public ResponseEntity<EmpresaResponse> cadastrar(
            @RequestBody @Valid CriarEmpresaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // Chama o serviço para cadastrar a empresa e obtém a resposta.
        EmpresaResponse response = empresaService.cadastrar(request);
        URI uri = uriBuilder.path("/empresas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Endpoint para listar todas as empresas.
    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {
        List<EmpresaResponse> empresas = empresaService.listarTodas();
        return ResponseEntity.ok(empresas);
    }

    // Endpoint para buscar uma empresa por ID.
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> buscarPorId(@PathVariable Long id) {
        EmpresaResponse response = empresaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}

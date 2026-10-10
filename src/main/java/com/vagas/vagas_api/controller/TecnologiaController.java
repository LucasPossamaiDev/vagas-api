package com.vagas.vagas_api.controller;

import com.vagas.vagas_api.dto.tecnologia.CriarTecnologiaRequest;
import com.vagas.vagas_api.dto.tecnologia.TecnologiaResponse;
import com.vagas.vagas_api.services.TecnologiaService;
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
@RequestMapping("/tecnologias")
public class TecnologiaController {

    private final TecnologiaService tecnologiaService;

    public TecnologiaController(TecnologiaService tecnologiaService) {
        this.tecnologiaService = tecnologiaService;
    }

    // Endpoint para cadastrar uma nova tecnologia.
    @PostMapping
    public ResponseEntity<TecnologiaResponse> cadastrar(
            @RequestBody @Valid CriarTecnologiaRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // Chama o serviço para cadastrar a tecnologia e obtém a resposta.
        TecnologiaResponse response = tecnologiaService.cadastrar(request);
        URI uri = uriBuilder.path("/tecnologias/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Endpoint para listar todas as tecnologias cadastradas.
    @GetMapping
    public ResponseEntity<List<TecnologiaResponse>> listarTodas() {
        List<TecnologiaResponse> tecnologias = tecnologiaService.listarTodas();
        return ResponseEntity.ok(tecnologias);
    }

    // Endpoint para buscar uma tecnologia pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<TecnologiaResponse> buscarPorId(@PathVariable Long id) {
        TecnologiaResponse response = tecnologiaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}

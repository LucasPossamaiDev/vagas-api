package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.empresa.CriarEmpresaRequest;
import com.vagas.vagas_api.dto.empresa.EmpresaResponse;
import com.vagas.vagas_api.exception.RecursoNaoEncontradoException;
import com.vagas.vagas_api.exception.RegraDeNegocioException;
import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.repository.EmpresaRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmpresaResponse cadastrar(CriarEmpresaRequest request) {
        if (empresaRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("Já existe uma empresa cadastrada com este e-mail.");
        }

        if (empresaRepository.existsByCnpj(request.cnpj())) {
            throw new RegraDeNegocioException("Já existe uma empresa cadastrada com este CNPJ.");
        }

        Empresa empresa = new Empresa();
        empresa.setNome(request.nome());
        empresa.setEmail(request.email());
        empresa.setCnpj(request.cnpj());
        empresa.setSenha(passwordEncoder.encode(request.senha()));

        Empresa empresaSalva = empresaRepository.save(empresa);
        return new EmpresaResponse(empresaSalva);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long id) {
        Empresa empresa = buscarEntidadePorId(id);
        return new EmpresaResponse(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listarTodas() {
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Empresa buscarEntidadePorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada com ID: " + id));
    }
}

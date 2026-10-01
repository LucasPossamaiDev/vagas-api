package com.vagas.vagas_api.services;

import com.vagas.vagas_api.dto.curriculo.CurriculoResponse;
import com.vagas.vagas_api.models.Curriculo;
import com.vagas.vagas_api.repository.CurriculoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurriculoService {

    private final CurriculoRepository curriculoRepository;
    private final PasswordEncoder passwordEncoder;

    public CurriculoService(CurriculoRepository curriculoRepository, PasswordEncoder passwordEncoder) {
        this.curriculoRepository = curriculoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    Curriculo curriculo = new Curriculo();
    curriculo.setResumo(request.resumo());
    curriculo.setArquivoPdfUrl(request.arquivos_pdf_url());
}

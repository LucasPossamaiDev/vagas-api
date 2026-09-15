package com.vagas.vagas_api.runner;

import com.vagas.vagas_api.models.Empresa;
import com.vagas.vagas_api.repository.EmpresaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TesteEmpresaRunner implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;

    public TesteEmpresaRunner(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Empresa empresa = new Empresa();
        empresa.setNome("Carmak");
        empresa.setEmail("carmak@gmail.com");
        empresa.setCnpj("12345678901234");
        empresa.setSenha("123456");

        Empresa salva = empresaRepository.save(empresa);

        System.out.println("Empresa salva com sucesso! ID gerado: " + salva.getId());
        System.out.println("Nome: " + salva.getNome());
        System.out.println("Criado em: " + salva.getCriadoEm());

    }
}

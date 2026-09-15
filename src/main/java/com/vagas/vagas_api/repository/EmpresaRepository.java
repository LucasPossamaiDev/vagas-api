package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}

package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    List<Vaga> findByEmpresaId(Long empresaId);

    List<Vaga> findByStatusIgnoreCase(String status);
}

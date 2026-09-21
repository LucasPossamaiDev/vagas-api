package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
}

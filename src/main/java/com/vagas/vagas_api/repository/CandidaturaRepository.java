package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
}

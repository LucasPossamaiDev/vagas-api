package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Tecnologia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TecnologiaRepository extends JpaRepository<Tecnologia, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<Tecnologia> findByNomeIgnoreCase(String nome);
}

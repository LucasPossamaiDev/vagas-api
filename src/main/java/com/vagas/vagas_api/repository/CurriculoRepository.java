package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {
}

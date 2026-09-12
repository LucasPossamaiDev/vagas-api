package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface empresasRepository extends JpaRepository<Empresa, Integer> {
}

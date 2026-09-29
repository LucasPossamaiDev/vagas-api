package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Empresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);
    Optional<Empresa> findByEmail(String email);
}

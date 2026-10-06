package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Candidatura;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    boolean existsByVagaIdAndUsuarioId(Long vagaId, Long usuarioId);

    @EntityGraph(attributePaths = {"vaga", "vaga.empresa", "vaga.tecnologias", "usuario"})
    List<Candidatura> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"vaga", "vaga.empresa", "vaga.tecnologias", "usuario"})
    List<Candidatura> findByVagaId(Long vagaId);

    @Override
    @EntityGraph(attributePaths = {"vaga", "vaga.empresa", "vaga.tecnologias", "usuario"})
    Optional<Candidatura> findById(Long id);
}

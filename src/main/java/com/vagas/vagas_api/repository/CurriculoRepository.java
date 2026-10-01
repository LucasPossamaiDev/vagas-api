package com.vagas.vagas_api.repository;

import com.vagas.vagas_api.models.Curriculo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {

    // 1. Verifica se o usuário já possui um currículo cadastrado (relação 1:1)
    boolean existsByUsuarioId(Long usuarioId);

    // 2. Busca pelo ID do usuário trazendo o Set<Tecnologia> e Usuario em uma só query (evita LazyInitialization)
    @EntityGraph(attributePaths = {"tecnologias", "usuario"})
    Optional<Curriculo> findByUsuarioId(Long usuarioId);

    // 3. Sobrescreve o findById padrão para também carregar o Set<Tecnologia> com join fetch
    @Override
    @EntityGraph(attributePaths = {"tecnologias", "usuario"})
    Optional<Curriculo> findById(Long id);
}


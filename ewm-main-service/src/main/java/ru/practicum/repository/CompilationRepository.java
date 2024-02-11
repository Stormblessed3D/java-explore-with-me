package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"events"})
    Page<Compilation> findAll(Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"events"})
    Page<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}

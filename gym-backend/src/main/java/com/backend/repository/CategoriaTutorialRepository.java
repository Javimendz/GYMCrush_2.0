package com.backend.repository;

import com.backend.domain.CategoriaTutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaTutorialRepository extends JpaRepository<CategoriaTutorial, Long> {
    Optional<CategoriaTutorial> findByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
package com.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Nutricion;

public interface NutricionRepository extends JpaRepository<Nutricion, Long> {

    Optional<Nutricion> findFirstByUsuarioIdOrderByIdDesc(Long usuarioId);

    Optional<Nutricion> findById(Long id);

    Page<Nutricion> findByUsuarioId(Long usuarioId, Pageable pageable);

    List<Nutricion> findByUsuarioIdOrderByFechaGeneracionDesc(Long usuarioId);
}
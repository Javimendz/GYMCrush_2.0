package com.backend.repository;

import java.util.List;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Dieta;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {

    List<Dieta> findByCategoriaDietaNombre(String nombreCategoria);

    // Para validaciones de integridad
    boolean existsByNombreIgnoreCase(String nombre);

    List<Dieta> findByCategoriaDietaNombreIgnoreCase(String nombreCategoria);
}

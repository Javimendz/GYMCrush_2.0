package com.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Acceso;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    Optional<Acceso> findFirstByUsuarioUsernameAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(String username);

    List<Acceso> findByUsuarioUsernameOrderByFechaHoraEntradaDesc(String username);

    // para contar usuarios para el aforo
    long countByFechaHoraSalidaIsNull();
}

//Paquete de repositorio
package com.backend.repository;
import java.util.Optional;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Role;
//Interfaz de repositorio para la entidad Usuario
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    
}
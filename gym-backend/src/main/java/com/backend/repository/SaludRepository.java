//Paquete
package com.backend.repository;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Salud;
import com.backend.domain.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaludRepository extends JpaRepository<Salud, Long> {

    // Para buscar todos los registros de un usuario, del más reciente al más
    // antiguo
    List<Salud> findByUsuarioOrderByFechaMedicionDesc(Usuario usuario); 
    Optional<Salud> findFirstByUsuarioOrderByFechaMedicionDesc(Usuario usuario);

}

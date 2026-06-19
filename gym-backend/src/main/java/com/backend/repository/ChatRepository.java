//Paquete
package com.backend.repository;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}

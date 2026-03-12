package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.model.Locatario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocatarioRepository extends JpaRepository<Locatario, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmailIgnoreCase(String email);
}

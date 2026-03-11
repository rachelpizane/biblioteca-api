package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface AutorRepository extends JpaRepository<Autor, UUID> {
    boolean existsByCpf(String cpf);
}

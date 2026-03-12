package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LivroRepository extends JpaRepository<Livro, UUID> {
    boolean existsByIsbn(String isbn);
}
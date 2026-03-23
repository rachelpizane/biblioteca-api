package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.model.Locatario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocatarioRepository extends JpaRepository<Locatario, UUID> {
    @Query("""
    SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
    FROM Locatario l
    WHERE l.cpf = :cpf
    AND (:id IS NULL OR l.id <> :id)
    """)
    boolean existsByCpfAndIdNotNullable(String cpf, UUID id);

    @Query("""
    SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
    FROM Locatario l
    WHERE lower(l.email) = lower(:email)
    AND (:id IS NULL OR l.id <> :id)
    """)
    boolean existsByEmailIgnoreCaseAndIdNotNullable(String email, UUID id);
}

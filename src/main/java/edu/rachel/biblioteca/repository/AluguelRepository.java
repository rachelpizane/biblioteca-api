package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, UUID> {

    boolean existsByLocatarioIdAndStatus(UUID locatarioId, StatusEnum status);

    void deleteByLocatarioId(UUID locatarioId);
}

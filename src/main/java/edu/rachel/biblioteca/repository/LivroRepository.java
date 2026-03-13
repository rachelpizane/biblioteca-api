package edu.rachel.biblioteca.repository;

import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LivroRepository extends JpaRepository<Livro, UUID> {
    boolean existsByIsbn(String isbn);

    @Query("""
    SELECT l.id
    FROM Livro l
    WHERE l.id IN :livrosIds
    """)
    List<UUID> findLivrosIdsByIdIn(@Param("livrosIds") List<UUID> livrosIds);

    @Query("""
    SELECT l.id
    FROM Livro l
    JOIN l.alugueis a
    WHERE l.id IN :livrosIds
      AND a.status = :status
    """)
    List<UUID> findLivrosIdsComAluguelEmAndamento(
            @Param("livrosIds") List<UUID> livrosIds, @Param("status") StatusEnum status
    );
}
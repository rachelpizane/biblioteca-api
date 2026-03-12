package edu.rachel.biblioteca.mock;

import edu.rachel.biblioteca.dto.AutorResumoDTO;
import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;

import java.time.LocalDate;
import java.util.*;

public class LivroMock {

    public static Livro getLivroMock(UUID idLivro, UUID idAutor) {
        Livro livro = getLivroMock(AutorMock.getAutorMock(idAutor));
        livro.setId(idLivro);

        return livro;
    }

    public static Livro getLivroMock(Autor autor) {
        LivroRequestDTO request = getLivroRequestDTOMock(List.of(autor.getId()));

        Livro livro = new Livro();
        livro.setNome(request.nome());
        livro.setIsbn(request.isbn());
        livro.setDataPublicacao(request.dataPublicacao());

        Set<Autor> autores = new HashSet<>();
        autores.add(autor);
        livro.setAutores(autores);

        return livro;
    }


    public static LivroRequestDTO getLivroRequestDTOMock(List<UUID> autoresIds) {
        return new LivroRequestDTO(
                "Livro Teste",
                "9783161484100",
                LocalDate.of(2022, 11, 21),
                autoresIds
        );
    }

    public static LivroResponseDTO getLivroResponseDTOMock(UUID idAutor) {
        Livro livro = getLivroMock(UUID.randomUUID(), idAutor);
        List<AutorResumoDTO> autores = livro.getAutores().stream()
                .map(autor -> new AutorResumoDTO(autor.getId(), autor.getNome()))
                .toList();

        return new LivroResponseDTO(
                livro.getId(),
                livro.getNome(),
                livro.getIsbn(),
                livro.getDataPublicacao(),
                autores
        );
    }
}

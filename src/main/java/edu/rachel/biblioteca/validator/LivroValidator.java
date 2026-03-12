package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@AllArgsConstructor
@Component
public class LivroValidator {
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public void validar(LivroRequestDTO request) {
        validarIsbnUnico(request.isbn());
        validarExistenciaAutores(request.autoresIds());
    }

    private void validarIsbnUnico(String isbn){
        if(livroRepository.existsByIsbn(isbn)) {
            throw new BusinessException("Já existe um livro cadastrado com o ISBN informado");
        }
    }

    private void validarExistenciaAutores(List<UUID> autoresIds) {
        List<UUID> idRecebidos = new ArrayList<>(autoresIds);

        List<Autor> autores = autorRepository.findAllById(autoresIds);

        List<UUID> idsEncontrados = autores.stream()
                .map(Autor::getId)
                        .toList();

        idRecebidos.removeAll(idsEncontrados);

        if (!idRecebidos.isEmpty()) {
            throw new NotFoundException("Autores não encontrados: " + idRecebidos);
        }
    }
}

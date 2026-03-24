package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.LivroAlugadoException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.repository.LivroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@AllArgsConstructor
@Component
public class LivroValidator {

    private final LivroRepository repository;
    private final AutorValidator autorValidator;
    private final LocatarioValidator locatarioValidator;

    public void validarCadastro(LivroRequestDTO request) {
        validarIsbnUnico(request.isbn());
        autorValidator.validarExistencia(request.autoresIds());
    }

    public void validarExistencia(List<UUID> livrosIds) {
        List<UUID> livrosIdsRecebidos = new ArrayList<>(livrosIds);
        List<UUID> livrosIdsEncontrados = repository.findLivrosIdsByIdIn(livrosIds);

        livrosIdsRecebidos.removeAll(livrosIdsEncontrados );

        if (!livrosIdsRecebidos.isEmpty()) {
            throw new NotFoundException("Livros não encontrados: " + livrosIdsRecebidos);
        }
    }

    public void validarAutorExistente(UUID autorId){
        autorValidator.validarExistencia(autorId);
    }

    public void validarLocatarioExistente(UUID locatarioId){
        locatarioValidator.validarExistencia(locatarioId);
    }

    public void validarLivrosDisponiveis(List<UUID> livrosIds) {
        List<UUID> livrosIdsAlugados = repository
                .findLivrosIdsComAluguelPorStatus(livrosIds, StatusEnum.EM_ANDAMENTO);

        if (!livrosIdsAlugados.isEmpty()) {
            throw new LivroAlugadoException("Livros com aluguel em andamento: " + livrosIdsAlugados);
        }
    }

    private void validarIsbnUnico(String isbn){
        if(repository.existsByIsbn(isbn)) {
            throw new BusinessException("Já existe um livro cadastrado com o ISBN informado");
        }
    }
}

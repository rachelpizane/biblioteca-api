package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
public class AutorValidator {
    private final AutorRepository repository;

    public void validarCadastro(AutorRequestDTO request) {
        if(repository.existsByCpf(request.cpf())) {
            throw new BusinessException("Já existe um autor cadastrado com o CPF informado");
        }
    }

    public void validarExistencia(UUID id) {
        if(!repository.existsById(id)) {
            throw new NotFoundException("Autor não encontrado");
        }
    }

    public void validarExistencia(List<UUID> autoresIds) {
        List<UUID> idRecebidos = new ArrayList<>(autoresIds);

        List<Autor> autores = repository.findAllById(autoresIds);

        List<UUID> idsEncontrados = autores.stream()
                .map(Autor::getId)
                .toList();

        idRecebidos.removeAll(idsEncontrados);

        if (!idRecebidos.isEmpty()) {
            throw new NotFoundException("Autores não encontrados: " + idRecebidos);
        }
    }
}

package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.repository.AutorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class AutorValidator {
    private final AutorRepository repository;

    public void validar(AutorRequestDTO request) {
        if(repository.existsByCpf(request.cpf())) {
            throw new BusinessException("Já existe um autor cadastrado com o CPF informado");
        }
    }

    public void validarExistencia(UUID id) {
        if(!repository.existsById(id)) {
            throw new NotFoundException("Autor não encontrado");
        }
    }
}

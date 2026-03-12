package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LocatarioValidator {
    private final LocatarioRepository repository;

    public void validar(LocatarioRequestDTO request) {
        validarCpfUnico(request.cpf());
        validarEmailUnico(request.email());
    }

    private void validarCpfUnico(String cpf){
        if(repository.existsByCpf(cpf)) {
            throw new BusinessException("Já existe um locatário cadastrado com o CPF informado");
        }
    }

    private void validarEmailUnico(String email){
        if(repository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Já existe um locatário cadastrado com o e-mail informado");
        }
    }
}

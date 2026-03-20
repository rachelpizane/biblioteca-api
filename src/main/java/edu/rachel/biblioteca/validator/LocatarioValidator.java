package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.ConflictBusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class LocatarioValidator {
    private final AluguelRepository aluguelRepository;
    private final LocatarioRepository locatarioRepository;

    public void validar(LocatarioRequestDTO request) {
        validarCpfUnico(request.cpf());
        validarEmailUnico(request.email());
    }

    public void validarExistencia(UUID id) {
        if(!locatarioRepository.existsById(id)) {
            throw new NotFoundException("Locatário não encontrado");
        }
    }

    public void validarLocatarioSemAluguelEmAndamento(UUID id){
        if(aluguelRepository.existsByLocatarioIdAndStatus(id, StatusEnum.EM_ANDAMENTO)) {
            throw new ConflictBusinessException("Locatário possui aluguéis em andamento");
        }
    }

    private void validarCpfUnico(String cpf){
        if(locatarioRepository.existsByCpf(cpf)) {
            throw new BusinessException("Já existe um locatário cadastrado com o CPF informado");
        }
    }

    private void validarEmailUnico(String email){
        if(locatarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Já existe um locatário cadastrado com o e-mail informado");
        }
    }


}

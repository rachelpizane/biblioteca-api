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

    public void validarAtualizacao(UUID id, LocatarioRequestDTO request) {
        validarExistencia(id);
        validarCpfUnico(request.cpf(), id);
        validarEmailUnico(request.email(), id);
    }

    public void validarExclusao(UUID id) {
        validarExistencia(id);
        validarLocatarioSemAluguelEmAndamento(id);
    }

    public void validarExistencia(UUID id) {
        if(!locatarioRepository.existsById(id)) {
            throw new NotFoundException("Locatário não encontrado");
        }
    }

    private void validarLocatarioSemAluguelEmAndamento(UUID id){
        if(aluguelRepository.existsByLocatarioIdAndStatus(id, StatusEnum.EM_ANDAMENTO)) {
            throw new ConflictBusinessException("Locatário possui aluguéis em andamento");
        }
    }

    private void validarCpfUnico(String cpf) {
        validarCpfUnico(cpf, null);
    }

    private void validarEmailUnico(String email){
        validarEmailUnico(email, null);
    }

    private void validarCpfUnico(String cpf, UUID id) {
        if (locatarioRepository.existsByCpfAndIdNotNullable(cpf, id)) {
            throw new BusinessException("Já existe um locatário cadastrado com o CPF informado");
        }
    }

    private void validarEmailUnico(String email, UUID id){
        if(locatarioRepository.existsByEmailIgnoreCaseAndIdNotNullable(email, id)) {
            throw new BusinessException("Já existe um locatário cadastrado com o e-mail informado");
        }
    }
}

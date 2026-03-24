package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.StatusInvalidoException;
import edu.rachel.biblioteca.model.Aluguel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class AluguelValidator {

    private final LocatarioValidator locatarioValidator;
    private final LivroValidator livroValidator;

    public void validarCadastro(AluguelRequestDTO request) {
        locatarioValidator.validarExistencia(request.locatarioId());
        livroValidator.validarExistencia(request.livrosIds());
        livroValidator.validarLivrosDisponiveis(request.livrosIds());
    }

    public void validarAtualizacaoStatus(Aluguel aluguel, StatusEnum statusNovo) {
        validarStatusNaoRepetido(aluguel.getStatus(), statusNovo);
        validarProibicaoReabertura(statusNovo);
        validarAlteracaoSomenteSeEmAndamento(aluguel.getStatus(), statusNovo);
    }

    private void validarStatusNaoRepetido(StatusEnum statusAtual, StatusEnum statusNovo){
        if(statusAtual.equals(statusNovo)) {
            throw new StatusInvalidoException(statusNovo);
        }
    }

    private void validarProibicaoReabertura(StatusEnum statusNovo) {
        if(statusNovo.equals(StatusEnum.EM_ANDAMENTO)) {
            throw new StatusInvalidoException("Não é permitido reabrir um aluguel. Crie um novo");
        }
    }

    private void validarAlteracaoSomenteSeEmAndamento(StatusEnum statusAtual, StatusEnum statusNovo){
        if(!statusAtual.equals(StatusEnum.EM_ANDAMENTO)) {
            throw new StatusInvalidoException(statusAtual, statusNovo);
        }
    }
}

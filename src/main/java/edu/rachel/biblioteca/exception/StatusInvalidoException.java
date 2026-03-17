package edu.rachel.biblioteca.exception;

import edu.rachel.biblioteca.enums.StatusEnum;

public class StatusInvalidoException extends RuntimeException {

    public StatusInvalidoException(String message) {
        super(message);
    }

    public StatusInvalidoException(StatusEnum status) {
        super("O aluguel já está com status " + status);
    }

    public StatusInvalidoException(StatusEnum statusAtual, StatusEnum statusNovo) {
        super(String.format(
                "Não é possível alterar o status de um aluguel %s para %s",
                statusAtual, statusNovo
        ));
    }


}

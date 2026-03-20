package edu.rachel.biblioteca.exception;

public class LivroAlugadoException extends ConflictBusinessException {
    public LivroAlugadoException(String message) {
        super(message);
    }
}

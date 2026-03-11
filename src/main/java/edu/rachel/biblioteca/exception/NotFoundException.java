package edu.rachel.biblioteca.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entidade) {
        super(entidade + " não encontrado(a)");
    }
}

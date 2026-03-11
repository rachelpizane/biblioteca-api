package edu.rachel.biblioteca.exception;

public class EnumInvalidoException extends RuntimeException {
    public EnumInvalidoException(String nomeEnum, String valor) {
        super("Valor inválido para " + nomeEnum + ": " + valor);
    }
}
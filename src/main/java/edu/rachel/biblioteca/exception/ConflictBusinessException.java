package edu.rachel.biblioteca.exception;

public class ConflictBusinessException extends RuntimeException {
    public ConflictBusinessException(String message) {
        super(message);
    }
}

package ru.akirakozov.sd.refactoring.ex;

public class ApplicationBootstrapException extends RuntimeException {
    public ApplicationBootstrapException(String message) {
        super(message);
    }

    public ApplicationBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}

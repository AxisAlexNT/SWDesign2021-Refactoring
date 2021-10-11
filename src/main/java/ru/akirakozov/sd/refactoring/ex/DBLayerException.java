package ru.akirakozov.sd.refactoring.ex;

public class DBLayerException extends RuntimeException {
    public DBLayerException(String message) {
        super(message);
    }

    public DBLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}

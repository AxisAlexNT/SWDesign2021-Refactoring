package ru.akirakozov.sd.refactoring.db.ex;

/**
 * Describes an exception that has occurred on the database layer of application.
 */
public class DBLayerException extends RuntimeException {
    /**
     * Describes an error that has occurred on the database layer of application.
     *
     * @param message A message to be displayed.
     * @param cause   An exception that caused application to fail to start.
     */
    public DBLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}

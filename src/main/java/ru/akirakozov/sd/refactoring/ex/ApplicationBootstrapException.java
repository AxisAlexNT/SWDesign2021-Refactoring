package ru.akirakozov.sd.refactoring.ex;

/**
 * Describes an error that prevented start of the application.
 */
public class ApplicationBootstrapException extends RuntimeException {
    /**
     * Describes an error that prevented start of the application.
     *
     * @param message A message to be displayed.
     * @param cause   An exception that caused application to fail to start.
     */
    public ApplicationBootstrapException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

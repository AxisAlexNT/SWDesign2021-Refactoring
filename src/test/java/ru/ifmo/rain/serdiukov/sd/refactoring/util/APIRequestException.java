package ru.ifmo.rain.serdiukov.sd.refactoring.util;

public class APIRequestException extends Exception {
    public APIRequestException(String message) {
        super(message);
    }

    public APIRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ru.practicum.ewm.main.exception;

public class WrongTimeException extends RuntimeException {
    public WrongTimeException(String message) {
        super(message);
    }
}

package ru.practicum.ewm.main.exception;

public class EventNotExistException extends RuntimeException {
    public EventNotExistException(String message) {
        super(message);
    }
}

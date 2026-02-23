package ru.practicum.ewm.main.exception;

public class CategoryNotExistException extends RuntimeException {
    public CategoryNotExistException(String message) {
        super(message);
    }
}

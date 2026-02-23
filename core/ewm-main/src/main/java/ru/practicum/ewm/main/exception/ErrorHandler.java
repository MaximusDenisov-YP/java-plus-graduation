package ru.practicum.ewm.main.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error("404 Not Found: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        log.error("403 Forbidden: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.FORBIDDEN,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setReason("Incorrectly made request.");
        apiError.setMessage("Validation failed");
        apiError.setErrors(errors);
        apiError.setTimestamp(LocalDateTime.now());

        return apiError;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.error("500 Internal Server Error: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiError handleConstraintViolation(ConstraintViolationException e) {
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(WrongTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleWrongTimeException(final WrongTimeException e) {
        log.error("400 Bad Request: {}", e.getMessage(), e);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler(AlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyPublishedException(final AlreadyPublishedException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler(EventAlreadyCanceledException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventAlreadyCanceledException(final EventAlreadyCanceledException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.error("409 Conflict: {}", e.getMessage(), e);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Incorrectly made request.",
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler(CategoryNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotExistException(final CategoryNotExistException e) {
        log.error("404 Not Found: {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found",
                e.getMessage(),
                stackTrace
        );
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}

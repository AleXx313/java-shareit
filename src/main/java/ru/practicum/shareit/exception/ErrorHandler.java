package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ModelNotFoundException.class, UserHaveNotAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final RuntimeException e) {
        return new ErrorResponse("Данные отсутствуют!", e.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final RuntimeException e) {
        return new ErrorResponse("Уникальные данные уже имеются!", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidExceptions(final MethodArgumentNotValidException e) {
        return new ErrorResponse("Передан некорректный объект!", e.getMessage());
    }

    @ExceptionHandler(Error.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNotSpecializedExceptions(final Error e) {
        return new ErrorResponse("Передан некорректный объект!", e.getMessage());
    }
}

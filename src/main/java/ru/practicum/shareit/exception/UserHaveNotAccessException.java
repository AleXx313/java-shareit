package ru.practicum.shareit.exception;

public class UserHaveNotAccessException extends RuntimeException {

    public UserHaveNotAccessException(String message) {
        super(message);
    }
}

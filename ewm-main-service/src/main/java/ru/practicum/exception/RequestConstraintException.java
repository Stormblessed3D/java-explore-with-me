package ru.practicum.exception;

public class RequestConstraintException extends RuntimeException {
    public RequestConstraintException(String s) {
        super(s);
    }
}

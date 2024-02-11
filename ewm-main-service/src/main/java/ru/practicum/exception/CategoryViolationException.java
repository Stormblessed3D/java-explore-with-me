package ru.practicum.exception;

public class CategoryViolationException extends RuntimeException {
    public CategoryViolationException(String s) {
        super(s);
    }
}

package ru.skypro.homework.exception;

public class PasswordValidationException extends RuntimeException{
    public PasswordValidationException() {
    }

    public PasswordValidationException(String message) {
        super(message);
    }
}

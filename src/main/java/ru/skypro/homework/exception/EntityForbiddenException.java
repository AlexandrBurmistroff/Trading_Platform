package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EntityForbiddenException extends RuntimeException {
    public EntityForbiddenException() {
    }

    public EntityForbiddenException(String message) {
        super(message);
    }
}

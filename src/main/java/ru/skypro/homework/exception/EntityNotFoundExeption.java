package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundExeption extends RuntimeException{
    public EntityNotFoundExeption() {
    }

    public EntityNotFoundExeption(String message) {
        super(message);
    }
}

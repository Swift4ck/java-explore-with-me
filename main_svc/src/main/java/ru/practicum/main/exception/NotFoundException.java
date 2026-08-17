package ru.practicum.main.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String mes) {
        super(mes);
    }

}

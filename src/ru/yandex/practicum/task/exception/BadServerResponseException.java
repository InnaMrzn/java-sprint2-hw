package ru.yandex.practicum.task.exception;

public class BadServerResponseException extends RuntimeException{

    public BadServerResponseException (String message) {
        super(message);
    }
}

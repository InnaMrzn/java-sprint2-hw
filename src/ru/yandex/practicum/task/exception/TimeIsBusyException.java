package ru.yandex.practicum.task.exception;

public class TimeIsBusyException extends RuntimeException{
    public TimeIsBusyException (String message) {

        super(message);
    }
}

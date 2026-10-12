package br.com.devbeise.spring_boot_kanban.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
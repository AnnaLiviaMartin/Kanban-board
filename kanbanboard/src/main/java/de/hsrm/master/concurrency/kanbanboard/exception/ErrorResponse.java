package de.hsrm.master.concurrency.kanbanboard.exception;

public record ErrorResponse(
        ErrorType error,
        String message,
        Object details
) {
    public ErrorResponse(ErrorType error, String message) {
        this(error, message, null);
    }
}
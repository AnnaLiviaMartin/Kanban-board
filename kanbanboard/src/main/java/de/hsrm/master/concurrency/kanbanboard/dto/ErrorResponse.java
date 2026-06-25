package de.hsrm.master.concurrency.kanbanboard.dto;

import de.hsrm.master.concurrency.kanbanboard.exception.ErrorType;

public record ErrorResponse(
        ErrorType error,
        String message,
        Object details
) {
    public ErrorResponse(ErrorType error, String message) {
        this(error, message, null);
    }
}
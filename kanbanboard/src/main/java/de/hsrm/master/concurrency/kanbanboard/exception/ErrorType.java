package de.hsrm.master.concurrency.kanbanboard.exception;

public enum ErrorType {
    VALIDATION_ERROR, NOT_FOUND, WIP_LIMIT_EXCEEDED, OPTIMISTIC_LOCK_CONFLICT, TASK_LOCKED, INTERNAL_ERROR
}

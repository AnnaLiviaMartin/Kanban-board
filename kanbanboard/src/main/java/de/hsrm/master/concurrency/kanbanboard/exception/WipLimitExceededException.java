package de.hsrm.master.concurrency.kanbanboard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class WipLimitExceededException extends RuntimeException {
    private final int wipLimit;
    private final int currentCount;

    public WipLimitExceededException(String columnName, int wipLimit, int currentCount) {
        super(String.format("WIP-Limit überschritten: Spalte '%s' erlaubt maximal %d Task(s), hat bereits %d.", columnName, wipLimit, currentCount));
        this.wipLimit = wipLimit;
        this.currentCount = currentCount;
    }
}
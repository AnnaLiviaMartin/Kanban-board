package de.hsrm.master.concurrency.kanbanboard.task.dto;

import jakarta.validation.constraints.NotNull;

public record TaskMoveRequest(
        @NotNull Long targetColumnId,
        int targetPosition,
        @NotNull long version
) {}

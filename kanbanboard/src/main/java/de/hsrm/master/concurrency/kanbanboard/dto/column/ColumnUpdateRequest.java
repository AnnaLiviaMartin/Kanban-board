package de.hsrm.master.concurrency.kanbanboard.dto.column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ColumnUpdateRequest(@NotBlank @Size(min = 1, max = 80) String name, Integer wipLimit) {
}

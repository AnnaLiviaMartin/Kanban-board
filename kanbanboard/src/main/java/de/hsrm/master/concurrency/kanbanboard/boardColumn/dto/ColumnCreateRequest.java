package de.hsrm.master.concurrency.kanbanboard.boardColumn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ColumnCreateRequest(@NotBlank @Size(min = 1, max = 80) String name, @Min(value = 1) Integer wipLimit) {
}
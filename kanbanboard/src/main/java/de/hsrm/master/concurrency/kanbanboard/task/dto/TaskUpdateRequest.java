package de.hsrm.master.concurrency.kanbanboard.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskUpdateRequest(@NotBlank @Size(min = 1, max = 200) String title,
                                @Size(max = 2000) String description) {
}

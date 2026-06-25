package de.hsrm.master.concurrency.kanbanboard.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(@NotBlank @Size(min = 1, max = 200) String title, @Size(max = 2000) String description,
                                @Min(0) int position) {
}
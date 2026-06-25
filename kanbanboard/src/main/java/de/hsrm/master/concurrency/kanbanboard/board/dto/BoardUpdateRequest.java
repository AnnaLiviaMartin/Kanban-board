package de.hsrm.master.concurrency.kanbanboard.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardUpdateRequest(@NotBlank @Size(min = 1, max = 100) String name) {
}
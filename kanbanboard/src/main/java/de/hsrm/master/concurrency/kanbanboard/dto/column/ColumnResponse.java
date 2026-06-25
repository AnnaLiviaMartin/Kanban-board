package de.hsrm.master.concurrency.kanbanboard.dto.column;

import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskResponse;
import de.hsrm.master.concurrency.kanbanboard.entity.BoardColumn;

import java.util.List;

public record ColumnResponse(Long id, String name, Integer wipLimit, int position, long version,
                             List<TaskResponse> tasks) {
    public static ColumnResponse from(BoardColumn c) {
        return new ColumnResponse(c.getId(), c.getName(), c.getWipLimit(), c.getPosition(), c.getVersion(), c.getTasks().stream().map(TaskResponse::from).toList());
    }
}
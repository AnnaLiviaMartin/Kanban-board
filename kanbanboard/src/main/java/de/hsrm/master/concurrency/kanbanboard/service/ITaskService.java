package de.hsrm.master.concurrency.kanbanboard.service;

import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskMoveRequest;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskResponse;
import de.hsrm.master.concurrency.kanbanboard.dto.task.TaskUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.entity.Task;

import java.util.List;

public interface ITaskService {
    List<TaskResponse> getTasksForColumn(Long columnId);

    TaskResponse getTask(Long taskId);

    TaskResponse createTask(Long columnId, TaskCreateRequest request);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String sessionId);

    TaskResponse moveTask(Long taskId, TaskMoveRequest request, String sessionId);

    void deleteTask(Long taskId, String sessionId);

    Task saveTaskTransactional(Task task);

}

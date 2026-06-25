package de.hsrm.master.concurrency.kanbanboard.task.service;

import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskMoveRequest;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskResponse;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.task.entity.Task;

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

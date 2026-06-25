package de.hsrm.master.concurrency.kanbanboard.task.service;

import de.hsrm.master.concurrency.kanbanboard.stomp.StompService;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskCreateRequest;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskMoveRequest;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskResponse;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskUpdateRequest;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.BoardColumn;
import de.hsrm.master.concurrency.kanbanboard.task.entity.Task;
import de.hsrm.master.concurrency.kanbanboard.exception.OptimisticLockConflictException;
import de.hsrm.master.concurrency.kanbanboard.exception.ResourceNotFoundException;
import de.hsrm.master.concurrency.kanbanboard.exception.TaskLockedException;
import de.hsrm.master.concurrency.kanbanboard.exception.WipLimitExceededException;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.ColumnRepository;
import de.hsrm.master.concurrency.kanbanboard.task.entity.TaskRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TaskService implements ITaskService {

    private final ConcurrentHashMap<Long, ReentrantLock> columnLocks = new ConcurrentHashMap<>();
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ColumnRepository columnRepository;
    @Autowired
    private StompService stompService;
    @Autowired
    private TaskLockService taskLockService;

    @Transactional(readOnly = true)
    @Override
    public List<TaskResponse> getTasksForColumn(Long columnId) {
        findColumnOrThrow(columnId);
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId).stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public TaskResponse getTask(Long taskId) {
        return TaskResponse.from(findTaskOrThrow(taskId));
    }

    @Transactional
    @Override
    public TaskResponse createTask(Long columnId, TaskCreateRequest request) {
        BoardColumn column = findColumnOrThrow(columnId);
        ReentrantLock lock = getColumnLock(columnId);

        lock.lock();
        try {
            log.debug("Lock erworben für Column {} (createTask)", columnId);
            checkWipLimit(column);

            int nextPos = taskRepository.findMaxPositionByColumnId(columnId) + 1;
            Task task = new Task(request.title(), request.description(), nextPos);
            column.addTask(task);
            task = saveTaskTransactional(task);

            TaskResponse response = TaskResponse.from(task);
            stompService.taskCreated(response);
            return response;
        } finally {
            lock.unlock();
            log.debug("Lock freigegeben für Column {} (createTask)", columnId);
        }
    }

    @Transactional
    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String sessionId) {
        if (taskLockService.isLocked(taskId, sessionId)) {
            String owner = taskLockService.getLockOwner(taskId).orElse("unbekannt");
            throw new TaskLockedException(taskId, owner);
        }

        Task task = findTaskOrThrow(taskId);

        task.setTitle(request.title());
        task.setDescription(request.description());

        try {
            task = taskRepository.save(task);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            throw new OptimisticLockConflictException("Optimistic Lock Konflikt beim Speichern von Task " + taskId);
        }

        TaskResponse response = TaskResponse.from(task);
        stompService.taskUpdated(response);
        return response;
    }

    @Override
    public TaskResponse moveTask(Long taskId, TaskMoveRequest request, String sessionId) {
        if (taskLockService.isLocked(taskId, sessionId)) {
            String owner = taskLockService.getLockOwner(taskId).orElse("unbekannt");
            throw new TaskLockedException(taskId, owner);
        }

        Task task = findTaskOrThrowEager(taskId);
        BoardColumn sourceColumn = task.getColumn();
        Long sourceColumnId = sourceColumn.getId();
        Long targetColumnId = request.targetColumnId();

        if (sourceColumnId.equals(targetColumnId)) {
            return moveWithinColumn(task, request.targetPosition());
        } else {
            return moveBetweenColumns(task, sourceColumnId, targetColumnId, request);
        }
    }

    private TaskResponse moveWithinColumn(Task task, int targetPosition) {
        Long columnId = task.getColumn().getId();
        List<Task> tasks = taskRepository.findByColumnIdOrderByPositionAsc(columnId);

        tasks.remove(task);
        int clampedPos = Math.max(0, Math.min(targetPosition, tasks.size()));
        tasks.add(clampedPos, task);

        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
        }
        taskRepository.saveAll(tasks);

        TaskResponse response = TaskResponse.from(task);
        stompService.taskMoved(response);
        return response;
    }

    private TaskResponse moveBetweenColumns(Task task, Long sourceColumnId, Long targetColumnId, TaskMoveRequest request) {
        BoardColumn targetColumn = findColumnOrThrow(targetColumnId);

        long firstId = Math.min(sourceColumnId, targetColumnId);
        long secondId = Math.max(sourceColumnId, targetColumnId);
        ReentrantLock firstLock = getColumnLock(firstId);
        ReentrantLock secondLock = getColumnLock(secondId);

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                log.debug("Locks erworben für Columns {} und {} (moveTask)", firstId, secondId);

                checkWipLimit(targetColumn);

                BoardColumn sourceCol = task.getColumn();
                sourceCol.removeTask(task);
                rebalancePositions(sourceColumnId);

                List<Task> targetTasks = taskRepository.findByColumnIdOrderByPositionAsc(targetColumnId);
                int clampedPos = Math.max(0, Math.min(request.targetPosition(), targetTasks.size()));
                task.setPosition(clampedPos);
                targetColumn.addTask(task);

                targetTasks.add(clampedPos, task);
                for (int i = 0; i < targetTasks.size(); i++) {
                    targetTasks.get(i).setPosition(i);
                }
                taskRepository.saveAll(targetTasks);
                task = taskRepository.save(task);

                TaskResponse response = TaskResponse.from(task);
                stompService.taskMoved(response);
                return response;

            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
            log.debug("Locks freigegeben für Columns {} und {} (moveTask)", firstId, secondId);
        }
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId, String sessionId) {
        if (taskLockService.isLocked(taskId, sessionId)) {
            String owner = taskLockService.getLockOwner(taskId).orElse("unbekannt");
            throw new TaskLockedException(taskId, owner);
        }

        Task task = findTaskOrThrow(taskId);
        Long columnId = task.getColumn().getId();
        task.getColumn().removeTask(task);
        taskRepository.delete(task);

        rebalancePositions(columnId);
        taskLockService.releaseLock(taskId, sessionId);
        stompService.taskDeleted(taskId);
    }

    @Override
    public Task saveTaskTransactional(Task task) {
        return taskRepository.save(task);
    }

    private void checkWipLimit(BoardColumn column) {
        if (column.getWipLimit() != null) {
            int current = taskRepository.countByColumnId(column.getId());
            if (current >= column.getWipLimit()) {
                throw new WipLimitExceededException(column.getName(), column.getWipLimit(), current);
            }
        }
    }

    private void rebalancePositions(Long columnId) {
        List<Task> tasks = taskRepository.findByColumnIdOrderByPositionAsc(columnId);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
        }
        if (!tasks.isEmpty()) taskRepository.saveAll(tasks);
    }

    private ReentrantLock getColumnLock(Long columnId) {
        return columnLocks.computeIfAbsent(columnId, id -> new ReentrantLock());
    }

    private BoardColumn findColumnOrThrow(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(() -> new ResourceNotFoundException("Column nicht gefunden: " + columnId));
    }

    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task nicht gefunden: " + taskId));
    }


    private Task findTaskOrThrowEager(Long taskId) {
        Task task = findTaskOrThrow(taskId);
        task.getColumn().getId();
        return task;
    }
}

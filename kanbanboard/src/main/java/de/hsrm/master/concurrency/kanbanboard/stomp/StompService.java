package de.hsrm.master.concurrency.kanbanboard.stomp;

import de.hsrm.master.concurrency.kanbanboard.board.dto.BoardDetailResponse;
import de.hsrm.master.concurrency.kanbanboard.boardColumn.dto.ColumnResponse;
import de.hsrm.master.concurrency.kanbanboard.lock.LockEvent;
import de.hsrm.master.concurrency.kanbanboard.presence.dto.PresenceEvent;
import de.hsrm.master.concurrency.kanbanboard.task.dto.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StompService {

    // TODO in application.properties
    public static final String TOPIC_BOARDS   = "/topic/boards";
    public static final String TOPIC_COLUMNS  = "/topic/columns";
    public static final String TOPIC_TASKS    = "/topic/tasks";
    public static final String TOPIC_PRESENCE = "/topic/presence";
    public static final String TOPIC_LOCKS    = "/topic/locks";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void boardCreated(BoardDetailResponse board) {
        send(TOPIC_BOARDS, new WsEvent<>(WsEventType.BOARD_CREATED, board));
    }

    public void boardUpdated(BoardDetailResponse board) {
        send(TOPIC_BOARDS, new WsEvent<>(WsEventType.BOARD_UPDATED, board));
    }

    public void boardDeleted(Long boardId) {
        send(TOPIC_BOARDS, new WsEvent<>(WsEventType.BOARD_DELETED, boardId));
    }

    public void columnCreated(ColumnResponse column) {
        send(TOPIC_COLUMNS, new WsEvent<>(WsEventType.COLUMN_CREATED, column));
    }

    public void columnUpdated(ColumnResponse column) {
        send(TOPIC_COLUMNS, new WsEvent<>(WsEventType.COLUMN_UPDATED, column));
    }

    public void columnDeleted(Long columnId) {
        send(TOPIC_COLUMNS, new WsEvent<>(WsEventType.COLUMN_DELETED, columnId));
    }

    public void taskCreated(TaskResponse task) {
        send(TOPIC_TASKS, new WsEvent<>(WsEventType.TASK_CREATED, task));
    }

    public void taskUpdated(TaskResponse task) {
        send(TOPIC_TASKS, new WsEvent<>(WsEventType.TASK_UPDATED, task));
    }

    public void taskMoved(TaskResponse task) {
        send(TOPIC_TASKS, new WsEvent<>(WsEventType.TASK_MOVED, task));
    }

    public void taskDeleted(Long taskId) {
        send(TOPIC_TASKS, new WsEvent<>(WsEventType.TASK_DELETED, taskId));
    }

    public void presenceChanged(PresenceEvent event) {
        send(TOPIC_PRESENCE, new WsEvent<>(WsEventType.PRESENCE_CHANGED, event));
    }

    public void lockChanged(LockEvent event) {
        send(TOPIC_LOCKS, new WsEvent<>(WsEventType.LOCK_CHANGED, event));
    }

    private <T> void send(String topic, WsEvent<T> event) {
        log.debug("WS-Broadcast → {} : {}", topic, event.type());
        messagingTemplate.convertAndSend(topic, event);
    }
}

package de.hsrm.master.concurrency.kanbanboard.stomp;

public record WsEvent<T>(WsEventType type, T payload) {
}

package de.hsrm.master.concurrency.kanbanboard.dto;

public record WsEvent<T>(WsEventType type, T payload) {
}

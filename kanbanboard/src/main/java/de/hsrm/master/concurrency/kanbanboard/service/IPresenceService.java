package de.hsrm.master.concurrency.kanbanboard.service;

public interface IPresenceService {
    void sessionConnected(String sessionId);
    void sessionDisconnected(String sessionId);
}

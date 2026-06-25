package de.hsrm.master.concurrency.kanbanboard.presence.service;

public interface IPresenceService {
    void sessionConnected(String sessionId);
    void sessionDisconnected(String sessionId);
}

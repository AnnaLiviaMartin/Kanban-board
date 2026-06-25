package de.hsrm.master.concurrency.kanbanboard.controller;

import de.hsrm.master.concurrency.kanbanboard.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/presence")
public class PresenceController {

    @Autowired
    private PresenceService presenceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPresence() {
        Set<String> sessions = presenceService.getActiveSessions();
        return ResponseEntity.ok(Map.of("activeUserCount", sessions.size(), "sessionIds", sessions));
    }
}

package kr.adapterz.springboot.auth.session;

import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Value
public class Session {
    String sessionId;
    Long userId;
    Instant createdAt;
    Map<String, Object> attributes;

    public Session(Long userId) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.attributes = new ConcurrentHashMap<>();
    }
}


package kr.adapterz.springboot.auth.session;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class Session {
    String sessionId;
    Long userId;
    Instant createdAt;

    public Session(Long userId) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = Instant.now();
    }
}


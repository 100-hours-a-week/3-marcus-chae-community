package kr.adapterz.springboot.auth.session;

import jakarta.annotation.Nullable;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 세션 스토어를 관리하는 매니저
 */
@Component
public class SessionManager {

    private final Map<String, Session> sessionStore = new ConcurrentHashMap<>();

    public Session createSession(Long userId) {

        Session session = new Session(userId);
        sessionStore.put(session.getSessionId(), session);

        return session;
    }

    //세션 조회
    @Nullable
    public Session getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    /**
     * 세션 조회 (필수)
     *
     * @throws UnauthenticatedException 세션이 존재하지 않는 경우
     */
    public Session requireSession(String sessionId) {
        Session session = getSession(sessionId);
        if (session == null) {
            throw new UnauthenticatedException();
        }
        return session;
    }

    /**
     * 세션 만료 (로그아웃)
     */
    public void expire(String sessionId) {
        sessionStore.remove(sessionId);
    }
}
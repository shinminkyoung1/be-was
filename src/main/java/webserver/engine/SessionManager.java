package webserver.engine;

import db.SessionDatabase;
import db.SessionEntry;
import model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionManager {
    private static final int SESSION_TIMEOUT_MINUTES = 60; // 60분 후 만료

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        SessionDatabase.save(sessionId, new SessionEntry(user));
        return sessionId;
    }

    public static User getSessionUser(String sessionId) {
        SessionEntry entry = SessionDatabase.find(sessionId);
        if (entry == null) return null;

        if (isExpired(entry)) {
            SessionDatabase.remove(sessionId);
            return null;
        }

        entry.updateLastAccessedTime();
        return entry.getUser();
    }

    private static boolean isExpired(SessionEntry entry) {
        Duration duration = Duration.between(entry.getLastAccessTime(), LocalDateTime.now());
        return duration.toMinutes() >= SESSION_TIMEOUT_MINUTES;
    }

    public static String getSessionCookieValue(String sessionId) {
        return String.format("sid=%s; Path=/; HttpOnly", sessionId);
    }
}

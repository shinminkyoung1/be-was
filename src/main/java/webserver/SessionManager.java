package webserver;

import db.Database;
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
        SessionDatabase.save(sessionId, new SessionEntry(user.userId()));
        return sessionId;
    }

    public static User getSessionUser(String sessionId, Database database) {
        SessionEntry entry = SessionDatabase.find(sessionId);
        if (entry == null) return null;

        if (isExpired(entry)) {
            SessionDatabase.remove(sessionId);
            return null;
        }

        entry.updateLastAccessedTime();
        return database.findUserById(entry.getUserId());
    }

    public static User getLoginUser(String sessionId, Database database) {
        if (sessionId == null) {
            return null;
        }

        SessionEntry entry = SessionDatabase.find(sessionId);
        if (entry == null || isExpired(entry)) {
            return null;
        }

        String userId = entry.getUserId();
        return database.findUserById(userId);
    }

    public static boolean isExpired(SessionEntry entry) {
        Duration duration = Duration.between(entry.getLastAccessTime(), LocalDateTime.now());
        return duration.toMinutes() >= SESSION_TIMEOUT_MINUTES;
    }

    public static String getSessionCookieValue(String sessionId) {
        return String.format("sid=%s; Path=/; HttpOnly", sessionId);
    }

    public static User getUserBySessionId(String sessionId, Database database) {
        SessionEntry entry = SessionDatabase.find(sessionId);
        if (entry == null) return null;

        return database.findUserById(entry.getUserId());
    }
}

package db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionDatabase {
    private static final Map<String, SessionEntry> sessions = new ConcurrentHashMap<>();

    public static void save(String sessionId, SessionEntry entry) {
        sessions.put(sessionId, entry);
    }

    public static SessionEntry find(String sessionId) {
        if (sessionId == null) return null;
        return sessions.get(sessionId);
    }

    public static void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public static Map<String, SessionEntry> getAll() {
        return sessions;
    }
}

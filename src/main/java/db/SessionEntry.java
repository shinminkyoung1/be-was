package db;

import model.User;

import java.time.LocalDateTime;

public class SessionEntry {
    private final String userId;
    private LocalDateTime lastAccessTime;

    public SessionEntry(String userId) {
        this.userId = userId;
        this.lastAccessTime = LocalDateTime.now();
    }

    // 테스트용 생성자
    public SessionEntry(String userId, LocalDateTime lastAccessTime) {
        this.userId = userId;
        this.lastAccessTime = lastAccessTime;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void updateLastAccessedTime() {
        this.lastAccessTime = LocalDateTime.now();
    }
}

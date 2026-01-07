package db;

import model.User;

import java.time.LocalDateTime;

public class SessionEntry {
    private final User user;
    private LocalDateTime lastAccessTime;

    public SessionEntry(User user) {
        this.user = user;
        this.lastAccessTime = LocalDateTime.now();
    }

    // 테스트용 생성자
    public SessionEntry(User user, LocalDateTime lastAccessTime) {
        this.user = user;
        this.lastAccessTime = lastAccessTime;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void updateLastAccessedTime() {
        this.lastAccessTime = LocalDateTime.now();
    }
}

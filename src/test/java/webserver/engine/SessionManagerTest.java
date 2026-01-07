package webserver.engine;

import db.SessionEntry;
import model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    // 새로 만든 세션은 만료가 false
    @Test
    void sessionActiveTest() {
        User user = new User("tester", "1234", "name", "test@test.com");
        SessionEntry activeEntry = new SessionEntry(user);

        // 만료 여부 검증
        assertFalse(SessionManager.isExpired(activeEntry), "방금 생성된 세션은 false를 반환해야 함");
    }

    // 60분이 지난 세션은 만료(isExpired)가 true여야 한다
    @Test
    void sessionExpirationTest() {
        User user = new User("tester", "1234", "name", "test@test.com");
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(61);
        SessionEntry expiredEntry = new SessionEntry(user, pastTime);

        // 만료 여부 검증
        assertTrue(SessionManager.isExpired(expiredEntry), "61분이 지났으므로 true를 반환해야 함");
    }

    // 스케줄러 청소 로직이 만료된 세션만 골라 삭제하는지 테스트
    @Test
    void cleanupTaskTest() {
        User user = new User("tester", "1234", "name", null);

        // 정상 세션
        db.SessionDatabase.save("valid-sid", new SessionEntry(user));
        // 만료된 세션 1개 (61분 전 시간 주입)
        db.SessionDatabase.save("expired-sid", new SessionEntry(user, LocalDateTime.now().minusMinutes(61)));

        db.SessionDatabase.getAll().entrySet().removeIf(entry ->
                SessionManager.isExpired(entry.getValue())
        );

        assertEquals(1, db.SessionDatabase.getAll().size());
        assertNotNull(db.SessionDatabase.find("valid-sid"));
        assertNull(db.SessionDatabase.find("expired-sid"));
    }
}
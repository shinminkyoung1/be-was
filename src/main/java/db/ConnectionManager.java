package db;

import webserver.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {
    static {
        // 서버 시작 시 테이블 자동 생성
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. USERS 테이블
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                    "userId VARCHAR(50) PRIMARY KEY, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "profileImage VARCHAR(255) DEFAULT '/img/basic_profileImage.svg')");

            // 2. ARTICLE 테이블
            stmt.execute("CREATE TABLE IF NOT EXISTS ARTICLE (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "writer VARCHAR(50) NOT NULL, " +
                    "title VARCHAR(255)," +
                    "contents TEXT , " +
                    "imagePath VARCHAR(255), " +
                    "likeCount INT DEFAULT 0, " +
                    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 3. COMMENTS 테이블
            stmt.execute("CREATE TABLE IF NOT EXISTS COMMENT (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "articleId BIGINT NOT NULL, " +
                    "writer VARCHAR(50) NOT NULL, " +
                    "text TEXT NOT NULL, " +
                    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PW);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
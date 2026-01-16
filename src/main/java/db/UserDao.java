package db;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // 회원가입
    public void insert(User user) {
        String sql = "INSERT INTO USERS (userId, name, password, email, profileImage) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user.userId());
            pstmt.setString(2, user.name());
            pstmt.setString(3, user.password());
            pstmt.setString(4, user.email());
            pstmt.setString(5, user.profileImage());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    // ID로 유저 정보 찾기
    public User findUserById(String userId) {
        String sql = "SELECT * FROM USERS WHERE userId = ?";

        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("userId"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("profileImage")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
        return null;
    }

    // 정보 갱신
    public void update(User user) {
        String sql = "UPDATE USERS SET name = ?, email = ?, profileImage = ?, password = ? WHERE userId = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user.name());
            pstmt.setString(2, user.email());
            pstmt.setString(3, user.profileImage());
            pstmt.setString(4, user.password());
            pstmt.setString(5, user.userId());

            pstmt.executeUpdate();
            logger.debug("User updated in DB: {}", user.userId());

        } catch (SQLException e) {
            logger.error("DB Update Error (User): {}", e.getMessage());
        }
    }

    // 아이디 중복 체크
    public boolean existsByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE userId = ?";
        try (Connection connection = ConnectionManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking userId existence", e);
        }
        return false;
    }

    // 닉네임 중복 체크
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE name = ?";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking name existence", e);
        }
        return false;
    }
}
package db;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    // 회원가입
    public void insert(User user) {
        String sql = "INSERT INTO USERS (userId, name, password, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user.userId());
            pstmt.setString(2, user.name());
            pstmt.setString(3, user.password());
            pstmt.setString(4, user.email());

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
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }
        return null;
    }
}
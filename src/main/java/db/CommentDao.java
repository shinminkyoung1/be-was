package db;

import model.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    // 특정 게시글의 댓글 목록 조회 (최신순)
    public List<Comment> findAllByArticleId(Long articleId) {
        String sql = "SELECT * FROM COMMENT WHERE articleId = ? ORDER BY id DESC";
        List<Comment> comments = new ArrayList<>();

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getLong("id"),
                            rs.getLong("articleId"),
                            rs.getString("writer"),
                            rs.getString("text"),
                            rs.getTimestamp("createdAt").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get comment", e);
        }
        return comments;
    }

    // 댓글 작성
    public void insert(Comment comment) {
        String sql = "INSERT INTO COMMENT (articleId, writer, text, createdAt) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, comment.articleId());
            pstmt.setString(2, comment.writer());
            pstmt.setString(3, comment.text());
            pstmt.setTimestamp(4, Timestamp.valueOf(comment.createdAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save comment", e);
        }
    }
}

package db;

import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDao {
    private static final Logger logger = LoggerFactory.getLogger(Article.class);

    // 게시글 저장
    public void insert(Article article) {
        String sql = "INSERT INTO ARTICLE (writer, title, contents, imagePath) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, article.writer());
            pstmt.setString(2, article.title());
            pstmt.setString(3, article.contents());
            pstmt.setString(4, article.imagePath());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save article: " + e.getMessage());
        }
    }

    // 게시글 조회
    public List<Article> selectAll() {
        String sql = "SELECT * FROM ARTICLE ORDER BY createdAt DESC";
        List<Article> articles = new ArrayList<>();

        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Article article = new Article(
                        rs.getLong("id"),
                        rs.getString("writer"),
                        rs.getString("title"),
                        rs.getString("contents"),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getString("imagePath"),
                        rs.getInt("likeCount")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve article list: {}", e);
        }
        return articles;
    }

    // 최신 게시글 1개 조회
    public Article selectLatest() {
        String sql = "SELECT * FROM ARTICLE ORDER BY id DESC LIMIT 1";
        try (Connection connection = ConnectionManager.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Article(
                        rs.getLong("id"),
                        rs.getString("writer"),
                        rs.getString("title"),
                        rs.getString("contents"),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getString("imagePath"),
                        rs.getInt("likeCount")
                );
            }
        } catch (SQLException e) {
            logger.error("Failed to get recent article", e);
        }
        return null;
    }

    // 좋아요 수 증가
    public void updateLikeCount(Long id) {
        String sql = "UPDATE ARTICLE SET likeCount = likeCount + 1 WHERE id = ?";
        try (Connection connection = ConnectionManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setLong(1, id);
                pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("좋아요 업데이트 실패: " + e.getMessage());
        }
    }
}

package db;

import model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDao {

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
                        rs.getString("imagePath")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve article list: {}", e);
        }
        return articles;
    }
}

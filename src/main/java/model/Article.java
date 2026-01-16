package model;

import java.time.LocalDateTime;

public record Article(Long id, String writer, String title, String contents, LocalDateTime createdAt, String imagePath, Integer likeCount) {

    public Article {
        if (likeCount == null) {
            likeCount = 0;
        }
    }

    public Article(String writer, String title, String contents, String imagePath) {
        this(null, writer, title, contents, null, imagePath, 0);
    }
}
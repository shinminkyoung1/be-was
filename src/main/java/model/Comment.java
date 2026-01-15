package model;

import java.time.LocalDateTime;

public record Comment(Long id, Long articleId, String writer, String text, LocalDateTime createdAt) {
    public Comment(Long articleId, String writer, String text) {
        this(null, articleId, writer, text, LocalDateTime.now());
    }
}
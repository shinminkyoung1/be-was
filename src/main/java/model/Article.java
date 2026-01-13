package model;

import java.time.LocalDateTime;

public record Article(Long id, String writer, String title, String contents, LocalDateTime createdAt) {
    public Article(String writer, String title, String contents) {
        this(null, writer, title, contents, null);
    }
}
-- 회원 정보를 위한 테이블
CREATE TABLE IF NOT EXISTS USERS (
    userId      VARCHAR(50) PRIMARY KEY,
    password    VARCHAR(50) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    email       VARCHAR(50) NOT NULL
    );

-- 게시글 저장을 위한 테이블
CREATE TABLE IF NOT EXISTS ARTICLE (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer      VARCHAR(50) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    contents    TEXT NOT NULL,
    createdAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
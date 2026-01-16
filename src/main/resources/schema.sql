-- 1. 회원 정보를 위한 테이블
CREATE TABLE IF NOT EXISTS USERS (
                                     userId          VARCHAR(50) PRIMARY KEY,
    password        VARCHAR(50) NOT NULL,
    name            VARCHAR(50) NOT NULL,
    email           VARCHAR(100),
    profileImage    VARCHAR(255) DEFAULT '/img/basic_profileImage.svg'
    );

-- 2. 게시글 저장을 위한 테이블 (이미지 경로 및 좋아요 수 추가)
CREATE TABLE IF NOT EXISTS ARTICLE (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer      VARCHAR(50) NOT NULL,
    title       VARCHAR(255),
    contents    TEXT,
    imagePath   VARCHAR(255),           -- 게시글에 업로드된 이미지 경로
    likeCount   INT DEFAULT 0,          -- 좋아요 개수
    createdAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (writer) REFERENCES USERS(userId) ON DELETE CASCADE
    );

-- 3. 댓글 저장을 위한 테이블
CREATE TABLE IF NOT EXISTS COMMENTS (
                                        id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        articleId   BIGINT NOT NULL,        -- 어떤 게시글의 댓글인지
                                        writer      VARCHAR(50) NOT NULL,   -- 댓글 작성자 ID
    text        TEXT NOT NULL,          -- 댓글 내용
    createdAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (articleId) REFERENCES ARTICLE(id) ON DELETE CASCADE,
    FOREIGN KEY (writer) REFERENCES USERS(userId) ON DELETE CASCADE
    );
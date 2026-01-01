package utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestUtilsTest {

    @Test
    void parseUrl() {
        String requestLine = "GET /index.html HTTP/1.1";
        String url = HttpRequestUtils.parseUrl(requestLine);
        assertThat(url).isEqualTo("/index.html");
    }

    @Test
    void getFileExtensionTest() {
        assertEquals("html", HttpRequestUtils.getFileExtension("/index.html"));
        assertEquals("css", HttpRequestUtils.getFileExtension("/css/style.css"));
        assertEquals("js", HttpRequestUtils.getFileExtension("/js/app.js"));
        assertEquals("ico", HttpRequestUtils.getFileExtension("/favicon.ico"));
        assertEquals("png", HttpRequestUtils.getFileExtension("/images/logo.png"));
    }

    // 쿼리 스트링이 포함된 URL 확장자 추출 테스트
    @Test
    void getExtensionWithQueryTest() {
        assertEquals("html", HttpRequestUtils.getFileExtension("/index.html?hame=tester"));
        assertEquals("css", HttpRequestUtils.getFileExtension("/style.css?v=1.2.3"));

    }

    // 확장자가 없거나 잘못된 경우 빈 문자열 반환
    @Test
    void getFileExtensionExceptionTest() {
        // 확장자 없음
        assertEquals("", HttpRequestUtils.getFileExtension("/user/list"));
        // 점으로 끝나는 경우
        assertEquals("", HttpRequestUtils.getFileExtension("/user/list"));
        // 파일명이 없는 경로
        assertEquals("", HttpRequestUtils.getFileExtension("/"));
        // null 또는 빈 문자열
        assertEquals("", HttpRequestUtils.getFileExtension(null));
        assertEquals("", HttpRequestUtils.getFileExtension(""));
    }
}
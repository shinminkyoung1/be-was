package webserver;

import org.junit.jupiter.api.Test;
import webserver.meta.MimeType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MimeTypeTest {

    @Test
    void getContentTypeTest() {
        assertEquals("text/html;charset=utf-8", MimeType.getContentType("html"));
        assertEquals("text/css", MimeType.getContentType("css"));
        assertEquals("application/javascript", MimeType.getContentType("js"));
        assertEquals("image/png", MimeType.getContentType("png"));
        assertEquals("image/x-icon", MimeType.getContentType("ico"));
    }

    // 확장자가 대문자인 경우
    @Test
    void getContentTypeIgnoreCaseTest() {
        assertEquals("text/css", MimeType.getContentType("CSS"));
        assertEquals("text/html;charset=utf-8", MimeType.getContentType("HTML"));
    }

    // 지원하지 않는 확장자인 경우
    @Test
    void getContentTypeDefaultTest() {
        // 정의되지 않은 확장자
        assertEquals("application/octet-stream", MimeType.getContentType("exe"));
        assertEquals("application/octet-stream", MimeType.getContentType("pdf"));

        // 빈 문자열이나 null 대응
        assertEquals("application/octet-stream", MimeType.getContentType(""));
        assertEquals("application/octet-stream", MimeType.getContentType(null));
    }
}
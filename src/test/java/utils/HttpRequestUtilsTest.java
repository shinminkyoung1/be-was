package utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HttpRequestUtilsTest {

    @Test
    void parseUrl() {
        String requestLine = "GET /index.html HTTP/1.1";
        String url = HttpRequestUtils.parseUrl(requestLine);
        assertThat(url).isEqualTo("/index.html");
    }
}
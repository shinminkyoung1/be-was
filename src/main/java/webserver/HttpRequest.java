package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String url;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        // OutputStream을 문자열로 읽기 위한 보조 스트림 연결
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        // HTTP 요청의 첫 번째 줄 읽음
        String line = br.readLine();
        if (line == null) return;
        logger.debug("Request Line: {}", line);

        // 공백을 기준으로 헤더에서 첫 줄 분리한 후 필드에 할당
        String[] tokens = line.split(" ");
        if (tokens.length >= 3) {
            this.method = tokens[0];
            this.url = tokens[1];
            this.protocol = tokens[2];
        }

        // 나머지 헤더 정보 읽음
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            logger.debug("Header: {}", line);
        }
    }

    public String getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }
}
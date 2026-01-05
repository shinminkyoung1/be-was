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
    private String path;
    private String queryString;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        // OutputStream을 문자열로 읽기 위한 보조 스트림 연결
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        // Request Line 파싱
        String line = br.readLine();
        if (line == null) return;
        logger.debug("Request Line: {}", line);
        parseRequestLine(line);

        // Headers 파싱 및 저장 (Keep-Alive 판단 위함)
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            logger.debug("Header: {}", line);
            parseHeader(line);
        }
    }

    private void parseRequestLine(String line) {
        String[] tokens = line.split(" ");
        if (tokens.length >= 3) {
            this.method = tokens[0];
            this.url = tokens[1];
            this.protocol = tokens[2];

            this.path = HttpRequestUtils.parsePath(this.url);
            this.queryString = HttpRequestUtils.parseQueryString(this.url);
            this.params = HttpRequestUtils.parseParameters(this.queryString);
        }
    }

    private void parseHeader(String line) {
        // key-value 형태로 분리
        int index = line.indexOf(Config.HEADER_DELIMITER);
        if (index != -1) {
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 2).trim();
            headers.put(key, value);
        }
    }

    // Connection 헤더 확인
    public boolean isKeepAlive() {
        return "keep-alive".equalsIgnoreCase(headers.get("Connection"));
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getMethod() {
        return method;
    }
    public String getUrl() {
        return url;
    }
    public String getPath() {
        return path;
    }
    public String getQueryString() {
       return queryString;
    }
    public String getParameter(String name) {
        return params.get(name);
    }
}
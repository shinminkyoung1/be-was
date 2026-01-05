package webserver.engine;

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

    // 바디 길이를 저장
    private int contentLength = 0;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line = br.readLine();
        if (line == null) return;

        // Request Line 파싱
        parseRequestLine(line);

        // 나머지 헤더 정보 읽음
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            logger.debug("Header: {}", line);
            parseHeader(line);
        }

        if (this.method.equals("POST") && contentLength > 0) {
            try {
                String body = utils.IOUtils.readData(br, contentLength);
                logger.debug("POST Body: {}", body);

                Map<String, String> bodyParams = HttpRequestUtils.parseParameters(body);
                this.params.putAll(bodyParams);
            } catch (IOException e) {
                logger.error("Failed to read POST body: {}", e.getMessage());
                throw new IOException("Incomplete POST body data", e);
            }
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
        int index = line.indexOf(":");
        if (index != -1) {
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            headers.put(key, value);

            if ("Content-Length".equalsIgnoreCase(key)) {
                try {
                    this.contentLength = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    this.contentLength = 0;
                    logger.warn("Invalid Content-Length value: {}", value);
                }
            }
        }
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
    public int getContentLength() { return contentLength; }
}
package webserver;

import model.MultipartPart;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;
import utils.IOUtils;
import webserver.config.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String url;
    private String path;
    private String queryString;
    private String protocol;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private List<MultipartPart> multipartParts = new ArrayList<>();

    // 바디 길이를 저장
    private int contentLength = 0;
    // 바디 길이 존재 여부 저장
    private boolean isChunked = false;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line = br.readLine();
        if (line == null) return;

        // Request Line 파싱
        String[] tokens = line.split(" ");
        if (tokens.length >= 3) {
            this.method = tokens[0];
            this.url = tokens[1];
            this.protocol = tokens[2];
            this.path = HttpRequestUtils.parsePath(this.url);
            this.queryString = HttpRequestUtils.parseQueryString(this.url);
            this.params = HttpRequestUtils.parseParameters(this.queryString);
        }

        // 나머지 헤더 정보 읽음
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            Pair pair = HttpRequestUtils.parseHeader(line);
            if (pair != null) {
                headers.put(pair.key, pair.value);

                if ("Content-Length".equalsIgnoreCase(pair.key)) {
                    this.contentLength = Integer.parseInt(pair.value);
                }

                // 쿠키 파싱
                if ("Cookie".equalsIgnoreCase(pair.key)) {
                    this.cookies = HttpRequestUtils.parseCookies(pair.value);
                }
            }
        }

        if (contentLength > 0) {
            parseBody(in);
        }
    }

    private void parseBody(InputStream in) throws IOException {
        byte[] body = in.readNBytes(contentLength);
        String contentType = headers.get("Content-Type");

        if (contentType != null && contentType.contains("multipart/form-data")) {
            String boundary = contentType.split("boundary=")[1];
            this.multipartParts = HttpRequestUtils.parseMultipartBody(body, boundary);

            for (MultipartPart part : multipartParts) {
                if (!part.isFile()) {
                    params.put(part.name(), new String(part.data(), StandardCharsets.UTF_8));
                }
            }

        } else {
            String bodyStr = new String(body, StandardCharsets.UTF_8);
            this.params.putAll(HttpRequestUtils.parseParameters(bodyStr));
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

    private String readChunkedBody(BufferedReader br) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            int chunkSize = Integer.parseInt(line.trim(), 16);
            if (chunkSize == 0) break;

            char[] buffer = new char[chunkSize];
            br.read(buffer, 0, chunkSize);
            body.append(buffer);
            br.readLine();
        }
        return body.toString();
    }

    private boolean hasRequestBody() {
        return "POST".equalsIgnoreCase(this.method) || "PUT".equalsIgnoreCase(this.method) || "PATCH".equalsIgnoreCase(this.method);
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

    public int getContentLength() {
        return contentLength;
    }

    public String getCookie(String name) {
        if (this.cookies == null) return null;
        return cookies.get(name);
    }

    public List<MultipartPart> getMultipartParts() {
        return multipartParts;
    }
}
package webserver;

import exception.WebsServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private final DataOutputStream dos;

    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void fileResponse(String url) {
        File file = new File(Config.STATIC_RESOURCE_PATH + url);

        // 파일 존재 여부 확인 후 존재하지 않으면 404 응답 호출
        if (!file.exists() || file.isDirectory()) {
            send404Response();
            return;
        }

        try {
            byte[] body = Files.readAllBytes(file.toPath());
            addHeader("Content-Type", MimeType.getContentType(HttpRequestUtils.getFileExtension(url)) + ";charset=" + Config.UTF_8);
            addHeader("Content-Length", String.valueOf(body.length));

            writeResponse(HttpStatus.OK, body); // 공통 메서드로 추출
        } catch (IOException e) {
            logger.error("Error while serving file {}: {}", url, e.getMessage());
            send500Response();
        }
    }

    // 404 Not Found 응답 처리
    public void send404Response() {
        byte[] body = "404 File Not Found".getBytes(StandardCharsets.UTF_8);
        addHeader("Content-Type", "text/plain;charset=" + Config.UTF_8);
        addHeader("Content-Length", String.valueOf(body.length));
        writeResponse(HttpStatus.NOT_FOUND, body);
    }

    // 500 Internal Server Error 응답 처리
    public void send500Response() {
        byte[] body = "500 Internal Server Error".getBytes(StandardCharsets.UTF_8);
        addHeader("Content-Type", "text/plain;charset=" + Config.UTF_8);
        addHeader("Content-Length", String.valueOf(body.length));
        writeResponse(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }

    // 302 Found 응답 처리
    public void sendRedirect(String redirectUrl) {
        addHeader("Location", redirectUrl);
        writeResponse(HttpStatus.FOUND, new byte[0]); // 바디 없음
    }

    // 공통 응답 작성 로직
    private void writeResponse(HttpStatus status, byte[] body) {
        try {
            // Status Line
            dos.writeBytes("HTTP/1.1 " + status.toString() + " " + Config.CRLF);
            // Headers
            for (String key : headers.keySet()) {
                dos.writeBytes(key + ": " + headers.get(key) + Config.CRLF);
            }
            // Blank Line
            dos.writeBytes(Config.CRLF);
            // Body
            if (body.length > 0) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            logger.error("Response Write Error: {}", e.getMessage());
            throw new WebsServerException(HttpStatus.INTERNAL_SERVER_ERROR, "응답 전송 중 오류 발생");
        }
    }
}
package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HttpResponse {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void fileResponse(String url) {
        try {
            File file = new File(Config.STATIC_RESOURCE_PATH + url);

            // 파일 존재 여부 확인 후 존재하지 않으면 404 응답 호출
            if (!file.exists() || file.isDirectory()) {
                send404Response();
                return;
            }

            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = MimeType.getContentType(HttpRequestUtils.getFileExtension(url));

            writeResponse(HttpStatus.OK, contentType, body); // 공통 메서드로 추출
        } catch (IOException e) {
            logger.error("Error while serving file {}: {}", url, e.getMessage());
        }
    }

    // 404 Not Found 응답 처리
    public void send404Response() {
        try {
            byte[] body = "404 File Not Found".getBytes(StandardCharsets.UTF_8);
            writeResponse(HttpStatus.NOT_FOUND, "text/plain", body);
        } catch (IOException e) {
            logger.error("404 Response Error: {}", e.getMessage());
        }
    }

    // 500 Internal Server Error 응답 처리
    public void send500Response() {
        try {
            byte[] body = "500 Internal Server Error".getBytes(StandardCharsets.UTF_8);
            writeResponse(HttpStatus.INTERNAL_SERVER_ERROR, "text/plain", body);
        } catch (IOException e) {
            logger.error("500 Response Error: {}", e.getMessage());
        }
    }

    // 공통 응답 작성 로직
    private void writeResponse(HttpStatus status, String contentType, byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 " + status.toString() + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=" + Config.UTF_8 + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.toString() + " \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            logger.error("Redirect Error: {}", e.getMessage());
        }
    }
}
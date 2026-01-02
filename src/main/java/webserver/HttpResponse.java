package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HttpResponse {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void fileResponse(String url) {
        try {
            File file = new File("./src/main/resources/static" + url);

            // 파일 존재 여부 확인 후 존재하지 않으면 404 응답 호출
            if (!file.exists() || file.isDirectory()) {
                send404Response();
                return;
            }

            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = MimeType.getContentType(HttpRequestUtils.getFileExtension(url));

            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");

            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error("Error while serving file {}: {}", url, e.getMessage());
        }
    }

    // 2. 302 Found 응답 (리다이렉트)
    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            logger.error("Redirect Error: {}", e.getMessage());
        }
    }

    // 3. 404 Not Found 응답
    public void send404Response() {
        try {
            byte[] body = "404 File Not Found".getBytes();
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error("404 Response Error: {}", e.getMessage());
        }
    }
}
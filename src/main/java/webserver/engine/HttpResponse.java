package webserver.engine;

import exception.WebsServerException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;
import webserver.meta.Config;
import webserver.meta.HttpStatus;
import webserver.meta.MimeType;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

    public void sendError(HttpStatus status) {
        byte[] body = status.getErrorMessageBytes();
        writeHeaders("text/plain", body.length);
        writeResponse(status, body);
    }

    public void sendRedirect(String redirectUrl) {
        addHeader("Location", redirectUrl);
        writeResponse(HttpStatus.FOUND, new byte[0]); // 바디 없음
    }

    public void fileResponse(String url, User loginUser) {
        File file = new File(Config.STATIC_RESOURCE_PATH + url);
        if (!file.exists() || file.isDirectory()) {
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        try {
            byte[] body = Files.readAllBytes(file.toPath());

            // 동적 HTML 처리
            if (url.endsWith(".html")) {
                String content = new String(body, Config.UTF_8);

                Map<String, String> model = new HashMap<>();
                model.put("header_items", PageRender.renderHeader(loginUser));

                content = TemplateEngine.render(content, model);

                body = content.getBytes(Config.UTF_8);
            }
            String contentType = MimeType.getContentType(HttpRequestUtils.getFileExtension(url));
            writeHeaders(contentType, body.length);
            writeResponse(HttpStatus.OK, body);
        } catch (IOException e) {
            logger.error("Error while serving file {}: {}", url, e.getMessage());
            sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    // 공통 헤더 작성 로직
    private void writeHeaders(String contentType, int contentLength) {
        addHeader("Content-Type", contentType + ";charset=" + Config.UTF_8);
        addHeader("Content-Length", String.valueOf(contentLength));
    }
}
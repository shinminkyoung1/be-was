package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;
import webserver.config.Config;
import webserver.config.HttpStatus;
import webserver.config.MimeType;

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
    private HttpStatus status = HttpStatus.OK;
    private boolean isCommitted = false;

    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void sendError(HttpStatus status) {
        if (isCommitted) {
            logger.warn("The error page cannot be sent because the response has already started.");
            return;
        }
        this.status = status;
        byte[] body = status.getErrorMessageBytes();
        setHttpHeader("text/plain", body.length);
        processWrite(body);
    }

    public void sendRedirect(String redirectUrl) {
        this.status = HttpStatus.FOUND;
        addHeader("Location", redirectUrl);
        processWrite(new byte[0]); // 바디 없음
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
            this.status = HttpStatus.OK;
            setHttpHeader(contentType, body.length);
            processWrite(body);
        } catch (IOException e) {
            logger.error("Error while serving file {}: {}", url, e.getMessage());
            sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void processWrite(byte[] body) {
        try {
            // 헤더 전송
            if (!isCommitted) {
                writeStatusLine();
                writeAllHeaders();
                isCommitted = true;
            }

            // 바디 전송
            if (body != null && body.length > 0) {
                dos.write(body, 0, body.length);
            }
            dos.flush();

        } catch (IOException e) {
            handleWriteError(e);
        }
    }

    private void handleWriteError(IOException e) {
        logger.error("Exception during transfer: {}", e.getMessage());
        if (!isCommitted && status != HttpStatus.INTERNAL_SERVER_ERROR) {
            sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeStatusLine() throws IOException {
        dos.writeBytes("HTTP/1.1 " + status.toString() + Config.CRLF);
    }

    private void writeAllHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + Config.CRLF);
        }
        dos.writeBytes(Config.CRLF);
    }

    private void setHttpHeader(String contentType, int contentLength) {
        addHeader("Content-Type", contentType + ";charset=" + Config.UTF_8);
        addHeader("Content-Length", String.valueOf(contentLength));
    }
}
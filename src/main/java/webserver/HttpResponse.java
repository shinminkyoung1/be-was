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

import static webserver.config.MimeType.getContentType;

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
        if (isCommitted) return;
        this.status = status;

        String errorPagePath = "/error/" + status.getCode() + ".html";
        File file = new File(Config.STATIC_RESOURCE_PATH + errorPagePath);

        try {
            byte[] body;
            if (file.exists()) {
                body = Files.readAllBytes(file.toPath());
                setHttpHeader("text/html", body.length);
            } else {
                body = status.getMessage().getBytes(Config.UTF_8);
                setHttpHeader("text/plain", body.length);
            }
            processWrite(body);
        } catch (IOException e) {
            logger.error("Error while sending error page: {}", e.getMessage());
        }
    }

    public void sendRedirect(String redirectUrl) {
        this.status = HttpStatus.FOUND;
        addHeader("Location", redirectUrl);
        processWrite(new byte[0]); // 바디 없음
    }

    public void fileResponse(String url, User loginUser, Map<String, String> additionalModel) {
        File file = new File(Config.STATIC_RESOURCE_PATH + url);

        if (!file.exists() && !url.contains(".")) {
            file = new File(Config.STATIC_RESOURCE_PATH + url + ".html");
        }

        if (file.isDirectory()) {
            logger.warn("Request path is a directory: {}", url);
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        if (!file.exists()) {
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        try {
            byte[] body = Files.readAllBytes(file.toPath());
            String fileName = file.getName();

            // 동적 HTML 처리
            if (fileName.endsWith(".html")) {
                String content = new String(body, Config.UTF_8);

                Map<String, String> model = new HashMap<>();
                model.put("header_items", PageRender.renderHeader(loginUser));

                if (additionalModel != null) {
                    model.putAll(additionalModel);
                }

                logger.debug("Rendering HTML with model: {}", model.keySet());

                content = TemplateEngine.render(content, model);

                body = content.getBytes(Config.UTF_8);
            }

            String extension = HttpRequestUtils.getFileExtension(url);
            if (extension == null || extension.isEmpty() || url.contains("?")) {
                if (url.equals("/") || url.startsWith("/article") || url.startsWith("/?")) {
                    extension = "html";
                }
            }

            String contentType = getContentType(extension);
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
            if (!isCommitted) {
                // 헤더를 먼저 생성하여 전송
                StringBuilder sb = new StringBuilder();
                sb.append("HTTP/1.1 ").append(status.toString()).append(Config.CRLF);

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(Config.CRLF);
                }
                sb.append(Config.CRLF);

                dos.writeBytes(sb.toString());
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

    public void sendHtmlContent(String content) {
        try {
            byte[] body = content.getBytes(Config.UTF_8);
            this.status = HttpStatus.OK;
            setHttpHeader("text/html", body.length);
            processWrite(body);
        } catch (Exception e) {
            logger.error("Error while encoding HTML content: {}", e.getMessage());
            sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void fileResponseFromExternal(File file) {
        try {
            if (!file.exists()) {
                sendError(HttpStatus.NOT_FOUND);
                return;
            }

            byte[] body = Files.readAllBytes(file.toPath());

            // 파일 확장자를 통해 Content-Type 결정
            String fileName = file.getName();
            String extension = "";
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
                extension = fileName.substring(lastIndex + 1).toLowerCase(); // 소문자로 통일
            }

            String contentType = getContentType(extension);

            this.status = HttpStatus.OK;
            if (contentType.startsWith("image/")) {
                addHeader("Content-Type", contentType);
            } else {
                addHeader("Content-Type", contentType + ";charset=" + Config.UTF_8);
            }
            addHeader("Content-Length", String.valueOf(body.length));
            processWrite(body);

        } catch (IOException e) {
            logger.error("Error while serving external file {}: {}", file.getName(), e.getMessage());
            sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
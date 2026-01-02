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
            // URL에서 확장자 추출
            String extension = HttpRequestUtils.getFileExtension(url);

            // 확장자에 맞는 MIME 타입 결정
            String contentType = MimeType.getContentType(extension);

            // URL 기반 파일 읽기
            byte[] body = Files.readAllBytes(new File("./src/main/resources/static" + url).toPath());

            // response200Header 내용
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");

            // responseBody 내용
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

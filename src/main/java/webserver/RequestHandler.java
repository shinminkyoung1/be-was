package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // 요청 객체 생성
            HttpRequest request = new HttpRequest(in);

            // 응답 객체 생성
            HttpResponse response = new HttpResponse(out);

            String url = request.getUrl();
            if (url == null) return;

            // 기본 값 설정
            if (url.equals("/")) {
                url = "/index.html";
            }

            response.fileResponse(url);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

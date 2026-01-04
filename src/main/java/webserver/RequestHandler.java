package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import model.User;
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
            while(true) {
                // 요청 객체 생성
                HttpRequest request = new HttpRequest(in);

                // 더 이상 읽을 요청이 없으면 루프 종료
                if (request.getPath() == null) break;

                // 응답 객체 생성
                HttpResponse response = new HttpResponse(out);

                // 경로에 맞는 핸들러 있는지 확인
                Handler handler = RouteGuide.findHandler(request.getPath());

                if (handler != null) {
                    // 핸들러 있으면 해당 로직 수행
                    handler.process(request, response);
                } else {
                    // 없으면 정적 파일 서빙
                    response.fileResponse(resolveStaticPath(request.getPath()));
                }

                // Keep-Alive 여부에 따른 연결 종료 판단
                if (!request.isKeepAlive()) {
                    break; // Keep-Alive가 아니면 소켓 닫음
                }
                logger.debug("Keep-Alive: 유지 중");
            }
        } catch (IOException e) {
            logger.error("소켓 통신 에러: {}", e.getMessage());
        }
    }

    private String resolveStaticPath(String path) {
        if (path.equals("/") || path.isEmpty()) {
            return Config.DEFAULT_PAGE;
        }
        if (path.equals("/registration")) {
            return Config.REGISTRATION_PAGE;
        }
        return path;
    }
}

package webserver;

import java.io.*;
import java.net.Socket;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handler.Handler;
import webserver.handler.RouteGuide;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private final RouteGuide routeGuide;
    private final Database database;

    public RequestHandler(Socket connectionSocket, RouteGuide routeGuide, Database database) {
        this.connection = connectionSocket;
        this.routeGuide = routeGuide;
        this.database = database;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // 요청 객체 생성
            HttpRequest request = new HttpRequest(in);

            // 응답 객체 생성
            HttpResponse response = new HttpResponse(out);

            // 유저 정보 추출
            String sessionId = request.getCookie("sid");
            User loginUser = SessionManager.getLoginUser(sessionId, database);

            String path = request.getPath();
            if (path == null) return;

            // 인터셉트 진행해 특정 페이지 접근 제한
            if (!SecurityInterceptor.preHandler(path, loginUser)) {
                logger.debug("Access Denied: Redirecting to /login");
                response.sendRedirect("/login");
                return;
            }

            // 경로에 맞는 핸들러 있는지 확인
            Handler handler = routeGuide.findHandler(path);
            if (handler != null) {
                // 핸들러 있으면 해당 로직 수행
                handler.process(request, response);
            } else {
                // 없으면 정적 파일 서빙
                response.fileResponse(path, loginUser);
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

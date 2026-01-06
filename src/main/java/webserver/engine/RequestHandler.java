package webserver.engine;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.meta.Config;
import webserver.portal.Handler;
import webserver.portal.RouteGuide;

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

            String sid = request.getCookie("sid");
            db.SessionEntry entry = db.SessionDatabase.find(sid);

            if (entry != null) {
                model.User loginUser = entry.getUser();
                logger.debug("Authorized User: {} ({})", loginUser.name(), loginUser.userId());
            } else {
                logger.debug("Anonymous User Request");
            }

            String path = request.getPath();
            if (path == null) return;

            // 경로에 맞는 핸들러 있는지 확인
            Handler handler = RouteGuide.findHandler(path);

            if (handler != null) {
                // 핸들러 있으면 해당 로직 수행
                handler.process(request, response);
            } else {
                // 없으면 정적 파일 서빙
                response.fileResponse(resolveStaticPath(path));
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String resolveStaticPath(String path) {
        if (path.equals("/") || path.isEmpty()) { return Config.DEFAULT_PAGE; }
        if (path.equals("/registration")) { return Config.REGISTRATION_PAGE; }
        if (path.equals("/login")) { return Config.LOGIN_PAGE; }
        return path;
    }
}

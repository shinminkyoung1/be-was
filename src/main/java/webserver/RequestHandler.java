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
            // 요청 객체 생성
            HttpRequest request = new HttpRequest(in);

            // 응답 객체 생성
            HttpResponse response = new HttpResponse(out);

            String path = request.getPath();
            if (path == null) {
                return;
            }

            if (path.equals("/") || path.isEmpty()) {
                path = Config.DEFAULT_PAGE;
            }

            if (path.equals("/registration")) {
                path = Config.REGISTRATION_PAGE;
            }

            // 회원가입
            if (path.equals("/user/create") || path.equals("/create")) {
                // request에 파싱되어 있는 params에서 데이터 추출
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email")
                );

                // db에 저장
                db.Database.addUser(user);
                logger.debug("Saved User: {}", user);

                // 가입 후 이동할 곳
                path = "/index.html"; // 기본 설정
            }
            response.fileResponse(path);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

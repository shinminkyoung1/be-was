package webserver.portal;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.engine.HttpRequest;
import webserver.engine.HttpResponse;
import webserver.engine.SessionManager;
import webserver.meta.Config;
import webserver.meta.HttpStatus;

public class LoginRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
            return;
        }

        login(request, response);
    }

    private void login(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        User user = Database.findUserById(userId);

        if (user != null && user.password().equals(password)) {
            logger.debug("Login Success: {}", user.userId());

            // 로그인 생성 시 세션 ID 생성
            String sessionId = SessionManager.createSession(user);
            // 쿠키로 세션 보냄
            response.addHeader("Set-Cookie", SessionManager.getSessionCookieValue(sessionId));
            response.sendRedirect(Config.MAIN_PAGE);
        } else {
            logger.debug("Login Failed");
            response.sendRedirect(Config.LOGIN_PAGE);
        }
    }

    private void loginSuccess(User user, HttpResponse response) {
        logger.debug("login Success: {}", user.userId());

        // 로그인 성공하면 세션 만듦
        String sessionId = SessionManager.createSession(user);
        // 세션으로 쿠키 보냄
        response.addHeader("Set-Cookie", SessionManager.getSessionCookieValue(sessionId));
    }

    private void loginFailed(HttpResponse response) {
        logger.debug("Login Failed");
        response.sendRedirect(Config.LOGIN_PAGE);

    }
}

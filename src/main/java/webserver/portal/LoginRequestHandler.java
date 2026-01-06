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
            logger.warn("Invalid request method: {}. Only POST is allowed for login", request.getMethod());
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
            return;
        }

        login(request, response);
    }

    private void login(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        User user = Database.findUserById(userId);

        // 유저 없는 경우 로그인 실패
        if (user != null) {
            logger.debug("Login Failed: User ID '{}' not found in Database", userId);
            response.sendRedirect(Config.LOGIN_PAGE);
            return;
        }

        // 비밀번호 틀린 경우 로그인 실패
        if (user.password().equals(password)) {
            loginSuccess(user, response);
        } else {
            logger.debug("Login Failed: Wrong password for user '{}'", userId);
            response.sendRedirect(Config.LOGIN_PAGE);
        }
    }

    private void loginSuccess(User user, HttpResponse response) {
        logger.debug("Login Success: {}", user.userId());

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

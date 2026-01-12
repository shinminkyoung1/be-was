package webserver.handler;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.Config;
import webserver.config.HttpStatus;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.config.Pair;

import javax.xml.crypto.Data;

public class UserRequestHandler implements Handler {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final Database database;

    public UserRequestHandler(Database database) {
        this.database = database;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        // POST 메서드 검증 로직
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            logger.warn("Invalid request method: {}. Only POST is allowed for registration.", request.getMethod());
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
            return;
        }

        register(request, response);
    }

    private void register(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // 유효성 검사
        if (isAnyEmpty(userId, password, name)) {
            logger.warn("Registration failed: Missing required parameters.");
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        User user = new User(userId, password, name, email);
        database.addUser(user);
        logger.debug("Saved User: {}", user);
        response.sendRedirect(Config.DEFAULT_PAGE);
    }

    private boolean isAnyEmpty(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

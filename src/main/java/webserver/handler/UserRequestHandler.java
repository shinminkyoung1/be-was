package webserver.handler;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.Config;
import webserver.config.HttpStatus;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class UserRequestHandler implements Handler {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

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
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        Database.addUser(user);
        logger.debug("Saved User: {}", user);
        response.sendRedirect(Config.DEFAULT_PAGE);
    }
}

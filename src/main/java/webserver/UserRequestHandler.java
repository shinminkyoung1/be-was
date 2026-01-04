package webserver;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRequestHandler implements Handler{
    public static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        Database.addUser(user);
        logger.debug("User saved: {}", user);

        // 작업 완료 후 메인 페이지로 리다이렉트
        response.sendRedirect(Config.DEFAULT_PAGE);
    }
}

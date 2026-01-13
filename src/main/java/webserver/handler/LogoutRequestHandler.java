package webserver.handler;

import db.Database;
import db.SessionDatabase;
import db.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.config.Config;

public class LogoutRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(LogoutRequestHandler.class);

    private final UserDao userDao;

    public LogoutRequestHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sid = request.getCookie("sid");

        if (sid != null) {
            SessionDatabase.remove(sid);
            logger.debug("Logout: Session {} removed from Database", sid);

            response.addHeader("Set-Cookie", "sid=" + sid + "; Path=/; Max-Age=0");
        }
        response.sendRedirect(Config.DEFAULT_PAGE);
    }
}

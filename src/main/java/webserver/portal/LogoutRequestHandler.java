package webserver.portal;

import db.SessionDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.engine.HttpRequest;
import webserver.engine.HttpResponse;
import webserver.meta.Config;

public class LogoutRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(LogoutRequestHandler.class);

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

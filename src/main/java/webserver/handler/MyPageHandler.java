package webserver.handler;

import db.UserDao;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.SessionManager;
import webserver.config.Config;

public class MyPageHandler implements Handler {

    private final UserDao userDao;

    public MyPageHandler (UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, userDao);

        if (loginUser == null) {
            response.sendRedirect(Config.LOGIN_PAGE);
            return;
        }

        response.fileResponse(Config.MY_PAGE, loginUser, null);
    }
}

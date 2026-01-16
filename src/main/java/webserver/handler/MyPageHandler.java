package webserver.handler;

import db.UserDao;
import model.User;
import org.h2.mvstore.Page;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.PageRender;
import webserver.SessionManager;
import webserver.config.Config;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> model = new HashMap<>();
        model.put("header_items", PageRender.renderHeader(loginUser));
        model.put("user_profile_image", PageRender.renderProfile(loginUser));
        model.put("user_name", loginUser.name());

        response.fileResponse(Config.MY_PAGE, loginUser, model);
    }
}

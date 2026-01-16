package webserver.handler;

import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.SessionManager;
import webserver.config.AppConfig;
import webserver.config.Config;

import java.util.HashMap;
import java.util.Map;

public class CommentPageHandler implements Handler {

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String articleId = request.getParameter("articleId");

        Map<String, String> model = new HashMap<>();
        model.put("articleId", articleId);

        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, AppConfig.getUserDao());

        response.fileResponse(Config.COMMENT_PAGE, loginUser, model);
    }
}

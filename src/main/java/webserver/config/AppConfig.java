package webserver.config;

import db.ArticleDao;
import db.Database;
import db.UserDao;
import model.Article;
import model.User;
import webserver.SessionManager;
import webserver.handler.*;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private static final UserDao userDao = new UserDao();
    private static final ArticleDao articleDao = new ArticleDao();

    private static final Handler userHandler = new UserRequestHandler(userDao);
    private static final Handler loginHandler = new LoginRequestHandler(userDao);
    private static final Handler logoutHandler = new LogoutRequestHandler(userDao);

    private static final Handler articleWriteHandler = new ArticleWriteHandler(articleDao, userDao);
    private static final Handler articleIndexHandler = new ArticleIndexHandler(articleDao, userDao);

    private static final Handler myPageHandler = new MyPageHandler(userDao);
    private static final Handler profileUpdateHandler = new ProfileUpdateHandler(userDao);

    public static Map<String, Handler> getRouteMappings() {
        Map<String, Handler> mappings = new HashMap<>();

        mappings.put("/user/create", userHandler);
        mappings.put("/user/login", loginHandler);
        mappings.put("/user/logout", logoutHandler);

        mappings.put("/article/write", articleWriteHandler);
        mappings.put("/", articleIndexHandler);
        mappings.put("/index.html", articleIndexHandler);

        mappings.put("/mypage", myPageHandler);
        mappings.put("/user/update", profileUpdateHandler);

        Map<String, String> staticPages = Map.of(
                "/registration", Config.REGISTRATION_PAGE,
                "/login", Config.LOGIN_PAGE,
                "/article", Config.ARTICLE_PAGE
        );

        staticPages.forEach((path, filePath) ->
                mappings.put(path, (request, response) -> {
                    String sessionId = request.getCookie("sid");
                    User loginUser = SessionManager.getLoginUser(sessionId, userDao);
                    response.fileResponse(filePath, loginUser, null);
                })
        );
        return mappings;
    }

    public static UserDao getUserDao() {
        return userDao;
    }
}
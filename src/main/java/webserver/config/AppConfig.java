package webserver.config;

import db.ArticleDao;
import db.Database;
import model.Article;
import model.User;
import webserver.SessionManager;
import webserver.handler.*;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private static final Database database = new Database();
    private static final ArticleDao articleDao = new ArticleDao();

    private static final Handler userHandler = new UserRequestHandler(database);
    private static final Handler loginHandler = new LoginRequestHandler(database);
    private static final Handler logoutHandler = new LogoutRequestHandler(database);

    private static final Handler articleWriteHandler = new ArticleWriteHandler(articleDao);

    public static Map<String, Handler> getRouteMappings() {
        Map<String, Handler> mappings = new HashMap<>();

        mappings.put("/user/create", userHandler);
        mappings.put("/user/login", loginHandler);
        mappings.put("/user/logout", logoutHandler);

        mappings.put("/article/create", articleWriteHandler);

        Map<String, String> staticPages = Map.of(
                "/", Config.DEFAULT_PAGE,
                "/registration", Config.REGISTRATION_PAGE,
                "/login", Config.LOGIN_PAGE,
                "/mypage", Config.MY_PAGE
        );

        staticPages.forEach((path, filePath) ->
                mappings.put(path, (request, response) -> {
                    String sessionId = request.getCookie("sid");
                    User loginUser = SessionManager.getLoginUser(sessionId, database);
                    response.fileResponse(filePath, loginUser);
                })
        );
        return mappings;
    }

    public static Database getDatabase() {
        return database;
    }
}
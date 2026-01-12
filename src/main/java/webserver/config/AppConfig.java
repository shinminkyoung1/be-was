package webserver.config;

import db.Database;
import webserver.SessionManager;
import webserver.handler.Handler;
import webserver.handler.LoginRequestHandler;
import webserver.handler.LogoutRequestHandler;
import webserver.handler.UserRequestHandler;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private static final Database database = new Database();

    private static final Handler userHandler = new UserRequestHandler(database);
    private static final Handler loginHandler = new LoginRequestHandler(database);
    private static final Handler logoutHandler = new LogoutRequestHandler(database);

    public static Map<String, Handler> getRouteMappings() {
        Map<String, Handler> mappings = new HashMap<>();

        mappings.put("/user/create", userHandler);
        mappings.put("/user/login", loginHandler);
        mappings.put("/user/logout", logoutHandler);

        Map<String, String> staticPages = Map.of(
                "/", Config.DEFAULT_PAGE,
                "/registration", Config.REGISTRATION_PAGE,
                "/login", Config.LOGIN_PAGE,
                "/mypage", Config.MY_PAGE
        );

        staticPages.forEach((path, filePath) ->
                mappings.put(path, (request, response) -> response.fileResponse(filePath, SessionManager.getLoginUser(request.getCookie("sid"))))
        );
        return mappings;
    }
}
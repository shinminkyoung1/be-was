package webserver.handler;

import webserver.config.Config;

import java.util.HashMap;
import java.util.Map;

public class RouteGuide {
    private static final Map<String, Handler> mappings = new HashMap<>();

    static {
        // 경로와 연결
        Handler userHandler = new UserRequestHandler();
        Handler loginHandler = new LoginRequestHandler();
        Handler logoutHandler = new LogoutRequestHandler();
        // 회원가입
        mappings.put("/user/create", userHandler);
        // 로그인
        mappings.put("/user/login", loginHandler);
        // 로그아웃
        mappings.put("/user/logout", logoutHandler);

        // 정적 파일 연결
        mappings.put("/", (req, res) ->
                res.fileResponse(Config.DEFAULT_PAGE, req.getLoginUser())
        );
        mappings.put("/registration", (req, res) ->
                res.fileResponse(Config.REGISTRATION_PAGE, req.getLoginUser())
        );
        mappings.put("/login", (req, res) ->
                res.fileResponse(Config.LOGIN_PAGE, req.getLoginUser())
        );
        mappings.put("/mypage", (req, res) ->
                res.fileResponse(Config.MY_PAGE, req.getLoginUser())
        );
    }

    public static Handler findHandler(String path) {
        return mappings.get(path);
    }
}

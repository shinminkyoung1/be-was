package webserver.portal;

import java.util.HashMap;
import java.util.Map;

public class RouteGuide {
    private static final Map<String, Handler> mappings = new HashMap<>();

    static {
        // 경로와 연결
        Handler userHandler = new UserRequestHandler();
        Handler loginHandler = new LoginRequestHandler();
        // 회원가입
        mappings.put("/user/create", userHandler);
        // 로그인
        mappings.put("/user/login", loginHandler);
    }

    public static Handler findHandler(String path) {
        return mappings.get(path);
    }
}

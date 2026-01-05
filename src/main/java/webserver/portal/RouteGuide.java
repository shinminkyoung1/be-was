package webserver.portal;

import java.util.HashMap;
import java.util.Map;

public class RouteGuide {
    private static final Map<String, Handler> mappings = new HashMap<>();

    static {
        // 경로와 연결
        Handler userHandler = new UserRequestHandler();
        mappings.put("/user/create", userHandler);
        mappings.put("/create", userHandler);
    }

    public static Handler findHandler(String path) {
        return mappings.get(path);
    }
}

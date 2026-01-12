package webserver.handler;

import java.util.Map;

public class RouteGuide {
    private final Map<String, Handler> mappings;

    public RouteGuide(Map<String, Handler> mappings) {
        this.mappings = mappings;
    }

    public Handler findHandler(String path) {
        return mappings.get(path);
    }
}

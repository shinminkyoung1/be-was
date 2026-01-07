package webserver.engine;

import java.util.Map;

public class TemplateEngine {
    public static String render(String template, Map<String, String> data) {
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            // {{ key }} 형태를 찾아 value로 모두 교체
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
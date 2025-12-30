package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

    // 요청하는 정적 파일을 분리하는 로직
    public static String parseUrl(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        String[] tokens = requestLine.split(" ");

        if (tokens.length < 2) {
            return null;
        }

        return tokens[1];
    }
}

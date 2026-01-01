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

    public static String getFileExtension(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // 쿼리 스트링 제거
        String path = url.split("\\?")[0];
        // 마지막 . 위치 찾음
        int lastDotIndex = path.lastIndexOf('.');
        // 확장자 없거나 마지막이 . 인 경우 예외 처리
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }
}

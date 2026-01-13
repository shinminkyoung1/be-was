package utils;

import model.MultipartPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.Pair;

import java.util.*;

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

    // URL에서 Path 분리 (? 문자 기점으로 분리)
    public static String parsePath(String url) {
        if (url == null) {
            return null;

        }
        return url.split("\\?")[0];
    }

    // URL에서 Query String 분리 (? 문자 기점으로 분리)
    public static String parseQueryString(String url) {
        if (url == null || !url.contains("?")) {
            return null;
        }
        String[] parts = url.split("\\?");
        return (parts.length > 1) ? parts[1] : null;
    }

    // Query String을 분해 후 Map으로 변환
    public static Map<String, String> parseParameters(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> params = new HashMap<>();
        // &을 기준으로 각 파라미터 쌍 분리
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            // =를 기준으로 key와 value를 분리
            String[] tokens = pair.split("=");
            if (tokens.length == 2) {
                try {
                    // URL 디코딩 처리
                    String key = tokens[0];
                    String value = java.net.URLDecoder.decode(tokens[1], "UTF-8");
                    params.put(key, value);
                } catch (java.io.UnsupportedEncodingException e) {
                    logger.error("'Decoding error: {}", e.getMessage());
                }
            }
        }
        return params;
    }

    // 쿠키 문자열 파싱
    public static Map<String, String> parseCookies(String cookieHeaderValue) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeaderValue == null || cookieHeaderValue.isEmpty()) {
            return cookies;
        }

        String[] pairs = cookieHeaderValue.split(";"); // Idea~ 와 분리
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return cookies;
    }

    // 헤더 파싱
    public static Pair parseHeader(String headerLine) {
        int index = headerLine.indexOf(":");
        if (index == -1) return null;

        String key = headerLine.substring(0, index).trim();
        String value = headerLine.substring(index + 1).trim();
        return new Pair(key, value);
    }

    // 멀티파트 파싱
    public static List<MultipartPart> parseMultipartBody(byte[] body, String boundary) {
        List<MultipartPart> parts = new ArrayList<>();
        String delimiter = "--" + boundary;
        byte[] delimiterBytes = delimiter.getBytes();

        int start = 0;
        while ((start = findIndex(body, delimiterBytes, start)) != -1) {
            start += delimiterBytes.length;

            if (start + 1 < body.length && body[start] == '-' && body[start + 1] == '-') break;

            int headerEnd = findIndex(body, new byte[]{13, 10, 13, 10}, start);
            if (headerEnd == -1) break;

            String headerSection = new String(body, start + 2, headerEnd - start - 2);

            String name = extractAttribute(headerSection, "name");
            String fileName = extractAttribute(headerSection, "filename");
            String contentType = extractAttribute(headerSection, "Content-Type");

            int nextDelimiter = findIndex(body, delimiterBytes, headerEnd + 4);
            if (nextDelimiter == -1) break;

            byte[] partData = Arrays.copyOfRange(body, headerEnd + 4, nextDelimiter - 2);

            parts.add(new MultipartPart(name, fileName, contentType, partData));
            start = nextDelimiter;
        }
        return parts;
    }

    private static int findIndex(byte[] source, byte[] target, int start) {
        for (int i = start; i <= source.length - target.length; i++) {
            boolean match = true;
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    private static String extractAttribute(String header, String attr) {
        int start = header.indexOf(attr + "=\"");
        if (start == -1) return null;
        start += attr.length() + 2;
        return header.substring(start, header.indexOf("\"", start));
    }
}

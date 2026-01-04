package webserver;

import java.util.Arrays;

public enum MimeType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    SVG("svg", "image/svg+xml"),
    // 지원하지 않는 경우 다운로드 시도하거나 텍스트
    DEFAULT("", "application/octet-stream");

    private final String extension;
    private final String contentType;

    MimeType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String getContentType(String ext) {
        return Arrays.stream(values())
                .filter(m -> m.extension.equalsIgnoreCase(ext)) // 대소문자 구분 안 함
                .findFirst()
                .orElse(DEFAULT)
                .contentType;
    }
}

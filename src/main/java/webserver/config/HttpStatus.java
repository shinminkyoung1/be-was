package webserver.config;

import java.nio.charset.StandardCharsets;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public byte[] getErrorMessageBytes() {
        String bodyText = String.format("%d %s", code, message);
        return bodyText.getBytes(StandardCharsets.UTF_8);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}

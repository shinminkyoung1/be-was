package exception;

import webserver.meta.HttpStatus;

public class WebsServerException extends RuntimeException {
    private final HttpStatus status;

    public WebsServerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

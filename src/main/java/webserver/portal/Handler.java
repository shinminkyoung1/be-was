package webserver.portal;

import webserver.engine.HttpRequest;
import webserver.engine.HttpResponse;

public interface Handler {
    void process(HttpRequest request, HttpResponse response);
}

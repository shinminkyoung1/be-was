package webserver.handler;

import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Handler {
    void process(HttpRequest request, HttpResponse response);
}

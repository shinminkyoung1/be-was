package webserver;

public interface Handler {
    void process(HttpRequest request, HttpResponse response);
}

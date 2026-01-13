package webserver.handler;

import db.ArticleDao;
import model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.config.Config;

public class ArticleWriteHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleWriteHandler.class);

    private final ArticleDao articleDao;

    public ArticleWriteHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String writer = request.getParameter("writer");
        String title = request.getParameter("title");
        String contents = request.getParameter("contents");

        Article article = new Article(writer, title, contents);

        articleDao.insert(article);

        response.sendRedirect(Config.DEFAULT_PAGE);
    }
}

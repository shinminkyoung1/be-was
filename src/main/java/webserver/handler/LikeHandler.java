package webserver.handler;

import db.ArticleDao;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.config.Config;

public class LikeHandler implements Handler{
    private final ArticleDao articleDao;

    public LikeHandler(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            Long articleId = Long.parseLong(idParam);
            articleDao.updateLikeCount(articleId);

            response.sendRedirect("/article/index?id=" + articleId);
        } else {
            response.sendRedirect(Config.DEFAULT_PAGE);
        }
    }
}

package webserver.handler;

import db.ArticleDao;
import db.UserDao;
import model.Article;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.SessionManager;
import webserver.config.Config;

public class ArticleWriteHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleWriteHandler.class);

    private final UserDao userDao;
    private final ArticleDao articleDao;

    public ArticleWriteHandler(ArticleDao articleDao, UserDao userDao) {
        this.articleDao = articleDao;
        this.userDao = userDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, userDao);

        if (loginUser == null) {
            response.sendRedirect("/login");
            return;
        }

        String title = request.getParameter("title");
        String contents = request.getParameter("contents");
        String writer = loginUser.userId();

        Article article = new Article(writer, title, contents);

        articleDao.insert(article);

        logger.debug("Saved Article");
        response.sendRedirect(Config.DEFAULT_PAGE);
    }
}

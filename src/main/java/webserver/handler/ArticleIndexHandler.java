package webserver.handler;

import db.ArticleDao;
import db.UserDao;
import model.Article;
import model.User;
import org.h2.mvstore.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.*;
import webserver.config.AppConfig;
import webserver.config.Config;
import webserver.config.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleIndexHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleIndexHandler.class);

    private final ArticleDao articleDao;
    private final UserDao userDao;

    public ArticleIndexHandler(ArticleDao articleDao, UserDao userDao) {
        this.articleDao = articleDao;
        this.userDao = userDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, AppConfig.getUserDao());

        try {
            File file = new File(Config.STATIC_RESOURCE_PATH + "/index.html");
            String content = new String(Files.readAllBytes(file.toPath()), Config.UTF_8);

            Map<String, String> model = new HashMap<>();

            model.put("header_items", PageRender.renderHeader(loginUser));

            List<Article> articles = articleDao.selectAll();
            model.put("posts_list", PageRender.renderArticleList(articles, userDao));

            String renderedHtml = TemplateEngine.render(content, model);

            response.sendHtmlContent(renderedHtml);
        } catch (IOException e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

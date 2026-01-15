package webserver.handler;

import db.ArticleDao;
import db.CommentDao;
import db.UserDao;
import model.Article;
import model.Comment;
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
    private final CommentDao commentDao;

    public ArticleIndexHandler(ArticleDao articleDao, UserDao userDao, CommentDao commentDao) {
        this.articleDao = articleDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, AppConfig.getUserDao());

        try {
            Article latest = articleDao.selectLatest();

            Map<String, String> model = new HashMap<>();
            model.put("header_items", PageRender.renderHeader(loginUser));

            if (latest != null) {
                User writer = userDao.findUserById(latest.writer());
                List<Comment> comments = commentDao.findAllByArticleId(latest.id());

                model.put("posts_list", PageRender.renderLatestArticle(latest, writer, comments.size()));
                model.put("comment_list", PageRender.renderComments(comments));

                Long prevId = articleDao.findPreviousId(latest.id());
                Long nextId = articleDao.findNextId(latest.id());
                model.put("post_nav", PageRender.renderPostNav(prevId, nextId, latest.id()));

            } else {
                model.put("posts_list", "<div class='post'><p class='post__article'>등록된 게시글이 없습니다.</p></div>");
                model.put("comment_list", "");
                model.put("post_nav", "");
            }

            File file = new File(Config.STATIC_RESOURCE_PATH + "/index.html");
            String content = new String(Files.readAllBytes(file.toPath()), Config.UTF_8);
            String renderedHtml = TemplateEngine.render(content, model);

            response.sendHtmlContent(renderedHtml);

        } catch (IOException e) {
            logger.error("Index rendering error: ", e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

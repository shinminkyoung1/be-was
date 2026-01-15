package webserver.handler;

import db.CommentDao;
import model.Comment;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.SessionManager;
import webserver.config.AppConfig;
import webserver.config.Config;

public class CommentWriteHandler implements  Handler{
    private static final Logger logger = LoggerFactory.getLogger(CommentWriteHandler.class);

    private final CommentDao commentDao;

    public CommentWriteHandler(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, AppConfig.getUserDao());

        String contents = request.getParameter("contents");
        String articleIdStr = request.getParameter("articleId");

        if (articleIdStr == null || articleIdStr.isEmpty()) {
            logger.error("Failed to send articleId");
            response.sendRedirect(Config.DEFAULT_PAGE);
            return;
        }

        try {
            Long articleId = Long.parseLong(articleIdStr);
            commentDao.insert(new Comment(articleId, loginUser.userId(), contents));
        } catch (NumberFormatException e) {
            logger.error("Not correct format to articleId: {}", articleIdStr);
        }

        response.sendRedirect("/article/index?id=" + articleIdStr);
    }
}

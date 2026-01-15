package webserver.handler;

import db.ArticleDao;
import db.UserDao;
import model.Article;
import model.MultipartPart;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.SessionManager;
import webserver.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 게시글 작성 요청을 처리하는 핸들러 클래스
 * 사용자가 입력한 게시글 데이터와 업로드된 이미지를 저장
 */
public class ArticleWriteHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ArticleWriteHandler.class);

    private final UserDao userDao;
    private final ArticleDao articleDao;

    public ArticleWriteHandler(ArticleDao articleDao, UserDao userDao) {
        this.articleDao = articleDao;
        this.userDao = userDao;
    }

    /**
     * 게시글 작성 요청을 처리하여 DB에 저장하고 메인 페이지로 리다이렉트
     * * @param request  클라이언트의 HTTP 요청 정보 객체
     * @param response 서버의 HTTP 응답 제어 객체
     */
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
        String imagePath = null;

        for (MultipartPart part : request.getMultipartParts()) {
            if (part.isFile() && "image".equals(part.name()) && part.data().length > 0) {
                imagePath = saveUploadedFile(part);
            }
        }

        Article article = new Article(writer, title, contents, imagePath);

        articleDao.insert(article);

        logger.debug("Saved Article");
        response.sendRedirect(Config.DEFAULT_PAGE);
    }

    private String saveUploadedFile(MultipartPart part) {
        String uploadDir = Config.STATIC_RESOURCE_PATH + "/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir(); // 폴더 없으면 생성
        }

        String fileName = UUID.randomUUID().toString() + "_" + part.fileName();
        File file = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(part.data());
            logger.debug("File saved successfully: {}", file.getAbsolutePath());
            return "/uploads/" + fileName;
        } catch (IOException e) {
            logger.debug("File save error: {}", e.getMessage());
            return null;
        }
    }
}

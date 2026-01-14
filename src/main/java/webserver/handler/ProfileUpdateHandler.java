package webserver.handler;

import db.UserDao;
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

public class ProfileUpdateHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ProfileUpdateHandler.class);

    private final UserDao userDao;

    public ProfileUpdateHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("sid");
        User loginUser = SessionManager.getLoginUser(sessionId, userDao);

        String profileImagePath = loginUser.profileImage();

        for (MultipartPart part : request.getMultipartParts()) {
            if (part.isFile() && "profileImage".equals(part.name()) && part.data().length > 0) {
                profileImagePath = saveUploadedFile(part);
            }
        }

        User updatedUser = new User(loginUser.userId(), loginUser.password(), loginUser.name(), loginUser.email(), profileImagePath);
        userDao.update(updatedUser);

        response.sendRedirect(Config.DEFAULT_PAGE);
    }

    private String saveUploadedFile(MultipartPart part) {
        String uploadDir = Config.STATIC_RESOURCE_PATH + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + part.fileName();
        File file = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(part.data());
            return "/uploads/" + fileName;
        } catch (IOException e) {
            return null;
        }
    }
}

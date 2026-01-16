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

        String newName = request.getParameter("name");
        String newEmail = request.getParameter("email");
        String newPwd = request.getParameter("password");
        String newPwdConfirm = request.getParameter("password_confirm");
        String deleteImageFlag = request.getParameter("delete_image");

        if (newName == null || newName.trim().length() < 4) {
            logger.warn("Update failed: Name must be at least 4 characters.");
            response.addHeader("Set-Cookie", "update_error=invalid_name; Path=/; Max-Age=5");
            response.sendRedirect("/mypage");
            return;
        }

        String finalPassword = loginUser.password();

        // 비밀번호 변경
        if (newPwd != null && !newPwd.trim().isEmpty()) {
            if (newPwd.trim().length() < 4) {
                logger.warn("Update failed: Password must be at least 4 characters.");
                response.addHeader("Set-Cookie", "update_error=invalid_pwd; Path=/; Max-Age=5");
                response.sendRedirect("/mypage");
                return;
            }

            if (!newPwd.equals(newPwdConfirm)) {
                logger.warn("Profile update failed: Password confirmation mismatch for user '{}'", loginUser.userId());

                response.addHeader("Set-Cookie", "update_error=pwd_mismatch; Path=/; Max-Age=5");
                response.sendRedirect("/mypage");
                return;
            }

            finalPassword = newPwd;
            logger.debug("Password change requested and validated for user '{}'", loginUser.userId());
        }

        // 프로필 이미지 처리
        String profileImagePath = loginUser.profileImage();

        // 프로필 이미지 삭제
        if ("true".equals(deleteImageFlag)) {
            profileImagePath = "/img/basic_profileImage.svg";
        }

        // 이미지 업로드
        for (MultipartPart part : request.getMultipartParts()) {
            if (part.isFile() && "profileImage".equals(part.name()) && part.data().length > 0) {
                profileImagePath = saveUploadedFile(part);
            }
        }

        User updatedUser = new User(loginUser.userId(), finalPassword, newName, newEmail, profileImagePath);
        userDao.update(updatedUser);

        logger.debug("Profile updated for user: {}", loginUser.userId());
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

package webserver;

import model.User;

import java.util.List;

public class SecurityInterceptor {
    // 권한을 제한할 경로
    private static final List<String> restrictedPaths = List.of("/mypage", "/user/logout");

    public static boolean preHandler(String path, User loginUser) {
        if (restrictedPaths.contains(path)) {
            return loginUser != null;
        }
        return true;
    }
}

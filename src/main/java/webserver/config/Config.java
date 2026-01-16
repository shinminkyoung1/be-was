package webserver.config;

public class Config {
    public static final String STATIC_RESOURCE_PATH = "./src/main/resources/static";
    public static final String DEFAULT_PAGE = "/index.html";
    public static final String REGISTRATION_PAGE = "/registration/index.html";
    public static final String LOGIN_PAGE = "/login/index.html";
    public static final String MAIN_PAGE = "/main/index.html";
    public static final String MY_PAGE = "/mypage/index.html";
    public static final String ARTICLE_PAGE = "/article/index.html";
    public static final String COMMENT_PAGE = "/comment/index.html";
    public static final String UTF_8 = "utf-8";

    public static final String defaultProfileImage = "/img/basic_profileImage.svg";

    public static final String CRLF = "\r\n";
    public static final String HEADER_DELIMITER = ": ";

    // h2 database
    // TODO: .gitignore로 관리
    public static final String EXTERNAL_UPLOAD_PATH = "./was-images";
    public static final String DB_URL = "jdbc:h2:./db/jwp-was;MODE=MySQL;AUTO_SERVER=TRUE";
    public static final String DB_USER = "apple";
    public static final String DB_PW = "1q2w3e4r";
}

package webserver;

import db.UserDao;
import model.Article;
import model.User;

import java.util.List;

public class PageRender {
    public static String renderHeader(User loginUser) {
        StringBuilder sb = new StringBuilder();

        if (loginUser != null) {
            // 로그인 상태: 글쓰기 + 이름(마이페이지) + 로그아웃
            // [사용자 이름] 클릭 시 마이페이지 이동
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">") // 여기서 링크 시작
                    .append("<strong>").append(loginUser.name()).append("님</strong>")
                    .append("</a>") // 링크 끝
                    .append("</li>");

            // [글쓰기] 버튼
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/article\">글쓰기</a>")
                    .append("</li>");

            // [로그아웃] 버튼
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/user/logout\">로그아웃</a>")
                    .append("</li>");
        } else {
            // 비로그인 상태: 로그인 + 회원가입
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>")
                    .append("</li>")
                    .append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>")
                    .append("</li>");
        }
        return sb.toString();
    }

    public static String renderArticleList(List<Article> articles, UserDao userDao) {
        if (articles.isEmpty()) {
            return "<div class='post'><p class='post__article'>등록된 게시글이 없습니다.</p></div>";
        }

        StringBuilder sb = new StringBuilder();
        for (Article article : articles) {
            User writer = userDao.findUserById(article.writer());
            String profileImg = (writer != null) ? writer.profileImage() : "/img/basic_profileImage.svg";

            sb.append("<div class=\"post\">");

            // 작성자 정보
            sb.append("  <div class=\"post__account\">");
            sb.append("    <img class=\"post__account__img\" src=\"").append(profileImg).append("\" />");
            sb.append("    <p class=\"post__account__nickname\">").append(article.writer()).append("</p>");
            sb.append("  </div>");

            // 이미지 (imagePath 있을 경우만)
            if (article.imagePath() != null && !article.imagePath().isEmpty()) {
                sb.append("  <img class=\"post__img\" src=\"").append(article.imagePath()).append("\" />");
            }

            // 좋아요, 공유, 북마크
            sb.append("  <div class=\"post__menu\">");
            sb.append("    <ul class=\"post__menu__personal\">");
            sb.append("      <li><button class=\"post__menu__btn\"><img src=\"/img/like.svg\" /></button></li>");
            sb.append("      <li><button class=\"post__menu__btn\"><img src=\"/img/comment.svg\" /></button></li>");
            sb.append("    </ul>");
            sb.append("    <button class=\"post__menu__btn\"><img src=\"/img/bookMark.svg\" /></button>");
            sb.append("  </div>");

            // 4. 본문 내용
            sb.append("  <p class=\"post__article\">").append(article.contents()).append("</p>");

            sb.append("</div>");
        }

        return sb.toString();
    }
}

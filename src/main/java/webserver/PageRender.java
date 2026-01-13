package webserver;

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

    public static String renderArticleList(List<Article> articles) {
        if (articles.isEmpty()) {
            return "<div class='post'><p class='post__article'>등록된 게시글이 없습니다.</p></div>";
        }

        StringBuilder sb = new StringBuilder();
        for (Article article : articles) {
            sb.append("<div class=\"post\">")
                    .append("  <div class=\"post__account\">")
                    .append("    <img class=\"post__account__img\" src=\"./img/default-avatar.svg\" />")
                    .append("    <p class=\"post__account__nickname\">").append(article.writer()).append("</p>")
                    .append("  </div>")
                    .append("  <div class=\"post__title\" style=\"font-weight:bold; margin: 10px 0;\">")
                    .append(article.title())
                    .append("  </div>")
                    .append("  <p class=\"post__article\">").append(article.contents()).append("</p>")
                    .append("  <div class=\"post__menu\">")
                    .append("    <ul class=\"post__menu__personal\">")
                    .append("      <li><button class=\"post__menu__btn\"><img src=\"./img/like.svg\" /></button></li>")
                    .append("    </ul>")
                    .append("  </div>")
                    .append("</div>")
                    .append("<hr style=\"border: 0.5px solid #eee; margin: 40px 0;\">"); // 게시글 구분선
        }
        return sb.toString();
    }
}

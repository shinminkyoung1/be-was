package webserver;

import db.UserDao;
import model.Article;
import model.Comment;
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
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/article/form\">글쓰기</a>")
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

    public static String renderProfile(User user) {
        StringBuilder sb = new StringBuilder();

        String profileImage = (user.profileImage() != null && !user.profileImage().isEmpty())
                ? user.profileImage()
                : "/img/basic_profileImage.svg";

        sb.append("<img class=\"profile\" src=\"")
                .append(profileImage)
                .append("\" id=\"profile-preview\" style=\"width: 100%; height: 100%; border-radius: 50%; object-fit: cover;\"/>");

        return sb.toString();
    }

    // 최신 게시글 1개 렌더링
    public static String renderLatestArticle(Article article, User writer, int commentCount) {
        if (article == null) {
            return "<div class='post'><p class='post__article'>등록된 게시글이 없습니다.</p></div>";
        }

            StringBuilder sb = new StringBuilder();

            String profileImage = (writer.profileImage() != null && !writer.profileImage().isEmpty())
                    ? writer.profileImage()
                    : "/img/basic_profileImage.svg";

            sb.append("<div class=\"post\">");
            // 작성자 정보
            sb.append("  <div class=\"post__account\">")
                    .append("    <img class=\"post__account__img\" src=\"").append(profileImage).append("\" />")
                    .append("    <p class=\"post__account__nickname\">").append(article.writer()).append("</p>")
                    .append("  </div>");

            // 이미지
            if (article.imagePath() != null && !article.imagePath().isEmpty()) {
                sb.append("  <img class=\"post__img\" src=\"").append(article.imagePath()).append("\" />");
            }

            // 좋아요 카운트 & 댓글 개수 포함
            sb.append("  <div class=\"post__menu\">")
                    .append("    <ul class=\"post__menu__personal\">")
                    .append("      <li>")
                    .append("        <a href=\"/article/like?id=").append(article.id()).append("\">")
                    .append("          <button class=\"post__menu__btn\"><img src=\"/img/like.svg\" /></button>")
                    .append("        </a> ")
                    .append(article.likeCount())
                    .append("      </li>")

                    .append("      <li><button class=\"post__menu__btn\"><img src=\"/img/comment.svg\" /></button> ")
                    .append(commentCount).append("</li>")
                    .append("    </ul>")
                    .append("  </div>");

            // 본문
            sb.append("  <p class=\"post__article\">").append(article.contents()).append("</p>");
            sb.append("</div>");

            return sb.toString();
    }

    // 댓글 목록 렌더링 (최대 3개 노출 및 모든 댓글 보기 버튼)
    public static String renderComments(List<model.Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return "<li class=\"comment__item\"><p class=\"comment__item__article\">아직 댓글이 없습니다.</p></li>";
        }

        StringBuilder sb = new StringBuilder();
        int totalCount = comments.size();

        for (int i=0; i<totalCount; i++) {
            Comment c = comments.get(i);

            String hiddenClass = (i >= 3) ? "comment__item--hidden" : "";
            String hiddenStyle = (i >= 3) ? "style=\"display: none;\"" : "";

            sb.append("<li class=\"comment__item ").append(hiddenClass).append("\" ").append(hiddenStyle).append(">")
                    .append("  <div class=\"comment__item__user\">")
                    .append("    <img class=\"comment__item__user__img\" src=\"/img/basic_profileImage.svg\" />")
                    .append("    <p class=\"comment__item__user__nickname\">").append(c.writer()).append("</p>")
                    .append("  </div>")
                    .append("  <p class=\"comment__item__article\">").append(c.text()).append("</p>")
                    .append("</li>");
        }

            // 3개를 초과할 경우에만 '모든 댓글 보기' 버튼 추가
            if (totalCount > 3) {
                sb.append("<li class=\"comment__item\" style=\"list-style: none;\">")
                        .append("  <button id=\"show-all-btn\" onclick=\"showAllComments()\" class=\"btn btn_ghost btn_size_m\" style=\"width: 100%;\">")
                        .append("    모든 댓글 보기(").append(totalCount).append("개)")
                        .append("  </button>")
                        .append("</li>");
        }

        return sb.toString();
    }

    // 하단 네비게이션 렌더링 (이전 글 / 댓글 작성 / 다음 글)
    public static String renderPostNav(Long prevId, Long nextId, Long currentArticleId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<nav class=\"nav\">")
                .append("  <ul class=\"nav__menu\">");

        // [이전 글] 버튼
        if (prevId != null) {
            sb.append("    <li class=\"nav__menu__item\">")
                    .append("      <a class=\"nav__menu__item__btn\" href=\"/article/index?id=").append(prevId).append("\">")
                    .append("        <img class=\"nav__menu__item__img\" src=\"/img/ci_chevron-left.svg\" /> 이전 글")
                    .append("      </a>")
                    .append("    </li>");
        } else {
            // 이전 글이 없으면 비활성화 스타일 (또는 빈 태그)
            sb.append("    <li class=\"nav__menu__item disabled\"><span class=\"nav__menu__item__btn\">이전 글 없음</span></li>");
        }

        // [댓글 작성] 버튼 (모달 연결 또는 작성 페이지 이동)
        sb.append("    <li class=\"nav__menu__item\">")
                .append("      <a class=\"btn btn_ghost btn_size_m\" href=\"/comment?articleId=")
                .append(currentArticleId).append("\">댓글 작성</a>")
                .append("    </li>");

        // [다음 글] 버튼
        if (nextId != null) {
            sb.append("    <li class=\"nav__menu__item\">")
                    .append("      <a class=\"nav__menu__item__btn\" href=\"/article/index?id=").append(nextId).append("\">")
                    .append("        다음 글 <img class=\"nav__menu__item__img\" src=\"/img/ci_chevron-right.svg\" />")
                    .append("      </a>")
                    .append("    </li>");
        } else {
            sb.append("    <li class=\"nav__menu__item disabled\"><span class=\"nav__menu__item__btn\">다음 글 없음</span></li>");
        }

        sb.append("  </ul>")
                .append("</nav>");

        return sb.toString();
    }
}

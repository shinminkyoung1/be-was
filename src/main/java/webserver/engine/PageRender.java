package webserver.engine;

import model.User;

public class PageRender {
    public static String renderHeader(User loginUser) {
        StringBuilder sb = new StringBuilder();

        if (loginUser != null) {
            // 로그인 상태: 글쓰기 + 이름(마이페이지) + 로그아웃
            // [글쓰기] 버튼
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/article\">글쓰기</a>")
                    .append("</li>");

            // [사용자 이름] 클릭 시 마이페이지 이동
            sb.append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">") // 여기서 링크 시작
                    .append("<strong>").append(loginUser.name()).append("님</strong>")
                    .append("</a>") // 링크 끝
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
}

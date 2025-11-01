package kr.adapterz.springboot.post.dto;

import kr.adapterz.springboot.user.entity.User;

/**
 * 게시글 작성자 정보 (공개 정보만 포함).
 * 이거 뒤에 Response 붙여야 하려나...?
 */
public record AuthorInfo(Long userId, String nickname) {
    public static AuthorInfo from(User author) {
        return new AuthorInfo(author.getId(), author.getNickname());
    }
}

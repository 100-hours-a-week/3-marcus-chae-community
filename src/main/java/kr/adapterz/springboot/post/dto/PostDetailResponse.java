package kr.adapterz.springboot.post.dto;

import kr.adapterz.springboot.post.entity.Post;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        AuthorInfo author
) {
    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                AuthorInfo.from(post.getAuthor())
        );
    }
}

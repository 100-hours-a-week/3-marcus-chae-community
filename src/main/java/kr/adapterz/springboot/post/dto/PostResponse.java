package kr.adapterz.springboot.post.dto;

import kr.adapterz.springboot.comment.dto.CommentResponse;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.user.dto.AuthorInfo;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        AuthorInfo author,
        List<CommentResponse> comments,
        Long viewCount
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                AuthorInfo.from(post.getAuthor()),
                null,
                post.getViewCount()
        );
    }

    public static PostResponse from(Post post, List<CommentResponse> comments) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                AuthorInfo.from(post.getAuthor()),
                comments,
                post.getViewCount()
        );
    }
}

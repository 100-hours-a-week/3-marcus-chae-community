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
        Long viewCount,
        Long commentCount
) {
    public static PostResponse from(Post post) {
        return from(post, null, null);
    }

    public static PostResponse from(Post post, List<CommentResponse> comments) {
        return from(post, comments, null);
    }

    public static PostResponse from(Post post, List<CommentResponse> comments, Long commentCount) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                AuthorInfo.from(post.getAuthor()),
                comments,
                post.getViewCount(),
                commentCount != null ? commentCount : 0L
        );
    }
}

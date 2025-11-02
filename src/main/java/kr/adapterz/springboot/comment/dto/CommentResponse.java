package kr.adapterz.springboot.comment.dto;

import kr.adapterz.springboot.comment.entity.Comment;
import kr.adapterz.springboot.user.dto.AuthorInfo;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        AuthorInfo author,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                AuthorInfo.from(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}

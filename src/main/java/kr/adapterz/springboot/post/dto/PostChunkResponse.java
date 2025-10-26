package kr.adapterz.springboot.post.dto;

import kr.adapterz.springboot.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostChunkResponse(
        List<PostSummary> posts,
        CursorInfo cursor
) {
    public record PostSummary(
            Long id,
            String title,
            String authorNickname,
            LocalDateTime createdAt
    ) {
        public static PostSummary from(Post post) {
            return new PostSummary(
                    post.getId(),
                    post.getTitle(),
                    post.getAuthor().getNickname(),
                    post.getCreatedAt()
            );
        }
    }

    public record CursorInfo(
            LocalDateTime createdAt,
            Long id,
            boolean hasNext
    ) {}
}
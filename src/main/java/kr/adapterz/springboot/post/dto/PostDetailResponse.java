package kr.adapterz.springboot.post.dto;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {
}

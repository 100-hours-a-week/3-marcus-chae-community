package kr.adapterz.springboot.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank
        @Size(max = 26)
        String title,

        @NotBlank
        @Size(max = 15000) // MySQL TEXT 타입 고려한 한글 글자수 제한
        String content,

        @Size(max = 1024)
        byte[] image
) {
}

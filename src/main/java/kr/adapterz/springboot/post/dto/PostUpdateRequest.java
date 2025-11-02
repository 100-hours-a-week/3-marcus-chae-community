package kr.adapterz.springboot.post.dto;

import jakarta.validation.constraints.Size;
import kr.adapterz.springboot.common.validation.NullOrNotBlank;

public record PostUpdateRequest(
        @NullOrNotBlank
        @Size(max = 26)
        String title,

        @NullOrNotBlank
        @Size(max = 15000) // MySQL TEXT 타입 고려한 한글 글자수 제한
        String content
) {
}

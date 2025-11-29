package kr.adapterz.springboot.post.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // 필터와 인터셉터 비활성화
@ActiveProfiles("test")
@Sql(scripts = "/data/post-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("존재하는 게시글 ID로 조회 시 200 OK와 게시글 정보 반환")
    void getPost_Success() throws Exception {
        // given: 데이터는 @Sql로 이미 준비됨 (id=1 게시글)

        // when & then
        mockMvc.perform(get("/posts/1"))  // MockMvc는 context-path를 자동 처리하므로 servlet path만 명시
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.author.userId").value(1))
                .andExpect(jsonPath("$.author.nickname").value("testuser"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 조회 시 404 Not Found")
    void getPost_NotFound() throws Exception {
        // given: 존재하지 않는 ID

        // when & then
        mockMvc.perform(get("/posts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("게시글을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("잘못된 형식의 ID로 조회 시 400 Bad Request")
    void getPost_InvalidIdFormat() throws Exception {
        // given: 숫자가 아닌 ID

        // when & then
        mockMvc.perform(get("/posts/invalid"))
                .andExpect(status().isBadRequest());
    }
}
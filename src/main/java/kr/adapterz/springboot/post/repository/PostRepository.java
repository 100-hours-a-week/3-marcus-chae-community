package kr.adapterz.springboot.post.repository;

import kr.adapterz.springboot.post.entity.Post;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Window<Post> findFirst10ByOrderByCreatedAtDesc(ScrollPosition position);

    /**
     * fetch join 유발해서 호출 시 {@link Post#author}에 프록시 객체가 아닌 실제 객체가 들어가도록 조회하는 메서드
     * @param postId
     * @return
     */
    @EntityGraph(attributePaths = {"author"})
    Optional<Post> findWithAuthorById(Long postId);
}
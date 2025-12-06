package kr.adapterz.springboot.comment.repository;

import kr.adapterz.springboot.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 참고: 조회는 기본적으로 작성자가 eager fetch됨.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByPostId(Long id);

    @EntityGraph(attributePaths = {"author"})
    @Override
    Optional<Comment> findById(Long id);

    /**
     * 특정 게시글의 댓글 수를 조회
     */
    long countByPostId(Long postId);

    /**
     * 여러 게시글의 댓글 수를 배치로 조회 (N+1 문제 방지)
     */
    @Query("SELECT c.post.id as postId, COUNT(c) as count " +
           "FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<CommentCountProjection> countByPostIdIn(@Param("postIds") List<Long> postIds);

    /**
     * 배치 COUNT 쿼리 결과를 담는 Projection 인터페이스
     */
    interface CommentCountProjection {
        Long getPostId();
        Long getCount();
    }
}

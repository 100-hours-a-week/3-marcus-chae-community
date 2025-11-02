package kr.adapterz.springboot.comment.repository;

import kr.adapterz.springboot.comment.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

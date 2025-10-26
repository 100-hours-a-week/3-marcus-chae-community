package kr.adapterz.springboot.post.repository;

import kr.adapterz.springboot.post.entity.Post;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Window<Post> findFirst10ByOrderByCreatedAtDesc(ScrollPosition position);
}
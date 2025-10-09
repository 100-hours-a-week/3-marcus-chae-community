package kr.adapterz.springboot.comment.entity;

import jakarta.persistence.*;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}

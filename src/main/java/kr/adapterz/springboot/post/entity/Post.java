package kr.adapterz.springboot.post.entity;

import jakarta.persistence.*;
import kr.adapterz.springboot.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String image;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    public Post() {
    }

    // config.DevDataSeeder용 미니 생성자
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

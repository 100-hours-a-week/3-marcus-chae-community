package kr.adapterz.springboot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class File {
    @Id
    private String fileKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;
}

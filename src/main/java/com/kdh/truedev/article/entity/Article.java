package com.kdh.truedev.article.entity;

import com.kdh.truedev.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")

public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Setter
    @Column
    private String title;

    @Setter
    @Column(columnDefinition = "LONGTEXT")
    @Lob
    private String content;

    @Setter
    @Column
    private String image;

    @Setter
    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Setter
    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "article_created_at")
    @CreatedDate
    private LocalDateTime articleCreatedAt;

    @LastModifiedDate
    @Column(name = "article_edited_at")
    private LocalDateTime articleEditedAt;

    @Column
    @Builder.Default
    private Boolean isDeleted =false;

    @Setter
    @Column(columnDefinition = "LONGTEXT")
    @Lob
    private String aiMessage; // AI피드백 메시지 (isVerifi

    // ed = false일 경우)

    @Column
    @Setter
    private boolean isVerified;// AI로부터 팩트체크를 받았는지

    @Column
    @Builder.Default
    @Setter
    private boolean isCheck = false; //AI 검증 api를 성공적으로 호출했는지 여부

    public void softDelete() { this.isDeleted = true; }



}

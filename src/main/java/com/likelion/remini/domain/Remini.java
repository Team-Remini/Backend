package com.likelion.remini.domain;

import com.likelion.remini.jwt.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Remini {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="remini_id")
    private Long reminiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

    private String type;

    private String title;

    private String reminiImageUrl;

    private Boolean instantSave;

    private Integer step;

    private Long likesCount;

    @OneToMany(mappedBy = "remini")
    private List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "remini")
    private List<Section> sectionList = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @Builder
    public Remini(User user, String type, String title, String reminiImageUrl, Boolean instantSave, Integer step, Long likesCount, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.reminiImageUrl = reminiImageUrl;
        this.instantSave = instantSave;
        this.step = step;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}

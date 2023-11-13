package com.likelion.remini.domain;


import com.likelion.remini.dto.ReminiUpdateRequestDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "remini", cascade = CascadeType.ALL)
    private List<Section> sectionList = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @Builder
    public Remini(User user, String type, String title, String reminiImageUrl, Boolean instantSave, Integer step, Long likesCount, LocalDateTime createdDate, LocalDateTime modifiedDate, List<Section> sectionList) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.reminiImageUrl = reminiImageUrl;
        this.instantSave = instantSave;
        this.step = step;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.sectionList.addAll(sectionList);
    }

    //회고 수정 api 위해 만듦
    public void update(ReminiUpdateRequestDTO updateRequestDTO){


        this.type = updateRequestDTO.getType();
        this.title = updateRequestDTO.getTitle();
        this.reminiImageUrl = updateRequestDTO.getReminiImageUrl();
        this.instantSave = updateRequestDTO.getInstantSave();
        this.step = updateRequestDTO.getStep();
        this.modifiedDate = LocalDateTime.now();

        List<String> sectionTexts = updateRequestDTO.getSectionTexts();

        this.sectionList.clear();

        if(sectionTexts != null && !sectionTexts.isEmpty()){
            for(String sectionText : sectionTexts){
                Section section = Section.builder()
                        .text(sectionText)
                        .remini(this)
                        .build();
                this.sectionList.add(section);
            }
        }

    }
    //좋아요 api에 사용되는 likesCount 증가/감소 api
    public void incrementLikesCount() {
        this.likesCount++;
    }

    public void decrementLikesCount() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

}

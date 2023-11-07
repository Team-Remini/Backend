package com.likelion.remini.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="remini_id")
    private Remini remini;

    private String text;

    @Builder
    public Section(Remini remini, String text){
        this.remini = remini;

        this.text = text;
    }
}


package com.likelion.remini.dto;

import lombok.Builder;
import lombok.Data;


import java.util.List;
@Data
public class ReminiUpdateRequestDTO {
    private String type;
    private String title;
    private String reminiImageUrl;
    private Boolean instantSave;
    private Integer step;
    private List<String> sectionTexts;

    @Builder
    public ReminiUpdateRequestDTO(String type, String title, String reminiImageUrl, Boolean instantSave, Integer step, List<String> sectionTexts){
        this.type = type;
        this.title = title;
        this.reminiImageUrl = reminiImageUrl;
        this.instantSave = instantSave;
        this.step = step;
        this.sectionTexts = sectionTexts;
    }
}

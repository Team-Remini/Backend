package com.likelion.remini.dto;

import com.likelion.remini.domain.Section;
import lombok.Data;

import java.util.List;

@Data
public class ReminiRequestDTO {
    private String type;
    private String title;
    private String reminiImage;
    private Boolean instantSave;
    private List<String> sectionTexts;
    private Integer step;
}

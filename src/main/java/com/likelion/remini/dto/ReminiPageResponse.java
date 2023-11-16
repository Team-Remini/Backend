package com.likelion.remini.dto;

import com.likelion.remini.domain.Remini;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReminiPageResponse {

    private final String title;

    private final String reminiImage;

    private final long likesCount;

    private final LocalDateTime createdDate;

    private ReminiPageResponse(String title, String reminiImage, long likesCount, LocalDateTime createdDate) {
        this.title = title;
        this.reminiImage = reminiImage;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
    }

    public static ReminiPageResponse of(Remini remini, String reminiImage) {

        return new ReminiPageResponse(remini.getTitle(), reminiImage, remini.getLikesCount(),
                remini.getCreatedDate());
    }
}

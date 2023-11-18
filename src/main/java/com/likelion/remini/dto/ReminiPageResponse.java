package com.likelion.remini.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.remini.domain.Remini;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReminiPageResponse {

    @ApiModelProperty(value = "회고 ID", example = "1")
    private final Long reminiId;

    @ApiModelProperty(value = "회고 제목", example = "LIKELION 11기 중앙 해커톤 회고")
    private final String title;

    @ApiModelProperty(value = "회고 사진 URL", example = "https://remini-bucket.s3.ap-northeast-2.amazonaws.com/remini-sample.jpg")
    private final String reminiImage;

    @ApiModelProperty(value = "좋아요 개수", example = "50")
    private final long likesCount;

    @ApiModelProperty(value = "요청자의 해당 회고 좋아요 여부", example = "false")
    private final boolean isLiked;

    @ApiModelProperty(value = "생성일자", example = "2023-11-17T01:41:45.123456")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdDate;

    private ReminiPageResponse(Long reminiId, String title, String reminiImage, long likesCount, boolean isLiked,
                               LocalDateTime createdDate) {
        this.reminiId = reminiId;
        this.title = title;
        this.reminiImage = reminiImage;
        this.likesCount = likesCount;
        this.isLiked = isLiked;
        this.createdDate = createdDate;
    }

    public static ReminiPageResponse of(Remini remini, String reminiImage) {

        return new ReminiPageResponse(remini.getReminiId(), remini.getTitle(), reminiImage, remini.getLikesCount(),
                false, remini.getCreatedDate());
    }

    public static ReminiPageResponse of(Remini remini, String reminiImage, boolean isLiked) {

        return new ReminiPageResponse(remini.getReminiId(), remini.getTitle(), reminiImage, remini.getLikesCount(),
                isLiked, remini.getCreatedDate());
    }
}

package com.likelion.remini.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.Section;
import com.likelion.remini.domain.Type;
import com.likelion.remini.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class ReminiDetailResponse {

    @ApiModelProperty(value = "회고 ID", example = "1")
    private final Long reminiId;

    @ApiModelProperty(value = "회고 카테고리", example = "KPT")
    private final Type type;

    @ApiModelProperty(value = "회고 제목", example = "LIKELION 11기 중앙 해커톤 회고")
    private final String title;

    @ApiModelProperty(value = "작성자 닉네임", example = "레미니")
    private final String nickname;

    @ApiModelProperty(value = "작성자 프로필 사진", example = "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg")
    private final String profileImageURL;

    @ApiModelProperty(value = "회고 사진 URL", example = "https://remini-bucket.s3.ap-northeast-2.amazonaws.com/remini-sample.jpg")
    private final String reminiImage;

    @ApiModelProperty(value = "임시저장 여부", example = "false")
    private final boolean instantSave;

    @Builder.Default
    @ApiModelProperty(value = "상세 회고 배열", example = "[\n" +
            "        \"좋은 성과를 냈어요\",\n" +
            "        \"시간 분배가 어려웠어요\",\n" +
            "        \"시간 분배를 더욱 잘 해 더 좋은 성과를 내고 싶어요\"\n" +
            "]")
    private final List<String> sectionTexts = new ArrayList<>();

    @ApiModelProperty(value = "- 0인 경우 회고 유형 가이드라인\n" +
            "- 1 이상인 경우, Step-by-Step 회고에서 마지막으로 수정한 Step 번호", example = "2")
    private final int step;

    @ApiModelProperty(value = "좋아요 개수", example = "50")
    private final long likesCount;

    @ApiModelProperty(value = "요청자의 해당 회고 좋아요 여부", example = "false")
    private final boolean isLiked;

    @ApiModelProperty(value = "요청자와 소유자 일치 여부", example = "true")
    private final boolean isOwner;

    @ApiModelProperty(value = "생성일자", example = "2023.11.17")
    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDate createdDate;

    public static ReminiDetailResponse create(Remini remini, User requestor, String reminiImage, boolean isLiked) {

        User owner = remini.getUser();

        return ReminiDetailResponse.builder()
                .reminiId(remini.getReminiId())
                .type(remini.getType())
                .title(remini.getTitle())
                .nickname(owner.getNickname())
                .profileImageURL(owner.getProfileImageURL())
                .reminiImage(reminiImage)
                .instantSave(remini.getInstantSave())
                .sectionTexts(remini.getSectionList().stream()
                        .map(Section::getText)
                        .collect(Collectors.toList()))
                .step(remini.getStep())
                .likesCount(remini.getLikesCount())
                .isLiked(isLiked)
                .isOwner(owner.getId().equals(requestor.getId()))
                .createdDate(remini.getCreatedDate().toLocalDate())
                .build();
    }
}

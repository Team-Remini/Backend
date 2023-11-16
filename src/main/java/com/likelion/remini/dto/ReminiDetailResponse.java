package com.likelion.remini.dto;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.Section;
import com.likelion.remini.domain.User;
import com.likelion.remini.service.PresignedUrlService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class ReminiDetailResponse {

    private final String type;

    private final String title;

    private final String nickname;

    private final String reminiImage;

    private final boolean instantSave;

    @Builder.Default
    private final List<String> section = new ArrayList<>();

    private final int step;

    private final long likesCount;

    private final boolean isLiked;

    private final boolean isOwner;

    private final LocalDateTime createdDate;

    public static ReminiDetailResponse create(Remini remini, User requestor, String reminiImage, boolean isLiked) {

        User owner = remini.getUser();

        return ReminiDetailResponse.builder()
                .type(remini.getType())
                .title(remini.getTitle())
                .nickname(owner.getNickname())
                .reminiImage(reminiImage)
                .instantSave(remini.getInstantSave())
                .section(remini.getSectionList().stream()
                        .map(Section::getText)
                        .collect(Collectors.toList()))
                .step(remini.getStep())
                .likesCount(remini.getLikesCount())
                .isLiked(isLiked)
                .isOwner(owner.getId().equals(requestor.getId()))
                .createdDate(remini.getCreatedDate())
                .build();
    }
}

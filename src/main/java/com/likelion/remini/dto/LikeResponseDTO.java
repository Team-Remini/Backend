package com.likelion.remini.dto;

import lombok.Getter;

@Getter
public class LikeResponseDTO {
    private final boolean liked;

    public LikeResponseDTO(boolean liked) {
        this.liked = liked;
    }
}

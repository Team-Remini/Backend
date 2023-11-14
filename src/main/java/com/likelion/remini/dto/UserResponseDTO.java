package com.likelion.remini.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDTO {
    private String nickName;
    private String state;
    private String profileImageUrl;
    private LocalDateTime expirationDate;

    @Builder
    public UserResponseDTO(String nickName, String state, String profileImageUrl, LocalDateTime expirationDate){
        this.nickName = nickName;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
        this.expirationDate = expirationDate;
    }
}

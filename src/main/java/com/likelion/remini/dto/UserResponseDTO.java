package com.likelion.remini.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDTO {

    @ApiModelProperty(value = "닉네임", example = "레미니")
    private String nickName;

    @ApiModelProperty(value = "구독모델", example = "standard")
    private String state;

    @ApiModelProperty(value = "프로필 사진 URL", example = "https://remini-bucket.s3.ap-northeast-2.amazonaws.com/profile-sample.jpg")
    private String profileImageUrl;

    @ApiModelProperty(value = "구독만료일", example = "2023-11-31T23:59:59.123456")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;

    @Builder
    public UserResponseDTO(String nickName, String state, String profileImageUrl, LocalDateTime expirationDate){
        this.nickName = nickName;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
        this.expirationDate = expirationDate;
    }
}

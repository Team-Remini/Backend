package com.likelion.remini.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.remini.domain.State;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDTO {

    @ApiModelProperty(value = "닉네임", example = "레미니")
    private String nickName;

    @ApiModelProperty(value = "구독모델", example = "STANDARD")
    private State state;

    @ApiModelProperty(value = "만료시간 이후 구독모델", example = "STANDARD")
    private State toBeState;

    @ApiModelProperty(value = "프로필 사진 URL", example = "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg")
    private String profileImageUrl;

    @ApiModelProperty(value = "구독만료일", example = "2023-11-31T23:59:59.123456")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;

    @ApiModelProperty(value = "알람 시간", example = "2023-12-16T13:00:00.000Z")
    private LocalDateTime alarmTime;

    @Builder
    public UserResponseDTO(String nickName, State state, State toBeState, String profileImageUrl, LocalDateTime expirationDate, LocalDateTime alarmTime){
        this.nickName = nickName;
        this.state = state;
        this.toBeState = toBeState;
        this.profileImageUrl = profileImageUrl;
        this.expirationDate = expirationDate;
        this.alarmTime = alarmTime;
    }
}

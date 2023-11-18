package com.likelion.remini.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReminiResponse {

    @ApiModelProperty(value = "회고 ID", example = "1")
    private Long reminiId;

    @ApiModelProperty(value = "사진 업로드 URL", example = "https://remini-bucket.s3.ap-northeast-2.amazonaws.com/dd42bbff-5091-4698-98ae-57ea9acf794c.png")
    private String uploadUrl;

    public static ReminiResponse of(Long reminiId, String uploadUrl) {
        return new ReminiResponse(reminiId, uploadUrl);
    }
}

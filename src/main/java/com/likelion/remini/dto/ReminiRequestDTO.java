package com.likelion.remini.dto;

import com.likelion.remini.domain.Section;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReminiRequestDTO {

    @ApiModelProperty(value = "회고 카테고리", example = "kpt")
    private String type;

    @ApiModelProperty(value = "회고 제목", example = "LIKELION 11기 중앙 해커톤 회고")
    private String title;

    @ApiModelProperty(value = "회고 사진 URL", example = "https://remini-bucket.s3.ap-northeast-2.amazonaws.com/remini-sample.jpg")
    private String reminiImage;

    @ApiModelProperty(value = "임시저장 여부", example = "false")
    private Boolean instantSave;

    @ApiModelProperty(value = "상세 회고 배열", example = "[\n" +
            "        \"좋은 성과를 냈어요\",\n" +
            "        \"시간 분배가 어려웠어요\",\n" +
            "        \"시간 분배를 더욱 잘 해 더 좋은 성과를 내고 싶어요\"\n" +
            "]")
    private List<String> sectionTexts;

    @ApiModelProperty(value = "- 0인 경우 회고 유형 가이드라인\n" +
            "- 1 이상인 경우, Step-by-Step 회고에서 마지막으로 수정한 Step 번호", example = "2")
    private Integer step;
}

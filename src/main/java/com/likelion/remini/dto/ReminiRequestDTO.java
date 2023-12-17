package com.likelion.remini.dto;

import com.likelion.remini.domain.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminiRequestDTO {

    @ApiModelProperty(value = "회고 카테고리", example = "KPT")
    private Type type;

    @ApiModelProperty(value = "회고 제목", example = "LIKELION 11기 중앙 해커톤 회고")
    @Size(max = 25, message = "회고 제목은 최대 25자입니다.")
    @NotBlank(message = "회고 제목은 최소 하나의, 공백이 아닌 문자가 포함되어야 합니다.")
    private String title;

    @ApiModelProperty(value = "임시저장 여부", example = "false")
    private Boolean instantSave;

    @ApiModelProperty(value = "상세 회고 배열", example = "[\n" +
            "        \"좋은 성과를 냈어요\",\n" +
            "        \"시간 분배가 어려웠어요\",\n" +
            "        \"시간 분배를 더욱 잘 해 더 좋은 성과를 내고 싶어요\"\n" +
            "]")
    private List<@Size(max = 200, message = "회고 내용은 최대 200자입니다.") String> sectionTexts;

    @ApiModelProperty(value = "- 0인 경우 회고 유형 가이드라인\n" +
            "- 1 이상인 경우, Step-by-Step 회고에서 마지막으로 수정한 Step 번호", example = "2")
    @Min(value = 0, message = "step의 최소값은 0입니다.")
    private Integer step;
}

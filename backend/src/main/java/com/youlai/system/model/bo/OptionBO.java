package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问题选项对象")
public class OptionBO {
    @Schema(description = "选项ID")
    private Long optionId;

    @Schema(description = "选项名称")
    private String optionName;

    @Schema(description = "是否已选中")
    private Boolean hasChecked = false;

    @Schema(description = "数量")
    private Long studentCount;

    @Schema(description = "学生姓名列表")
    private String studentNameList;
}

package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClazzPageQuery extends BasePageQuery {
    @Schema(description="关键字(班级名称)")
    private String keywords;

    @Schema(description = "班主任ID")
    private Long managerId;

    @Schema(description = "所属年级")
    private Long gradeId;

    @Schema(description = "课程类型")
    private String clazzType;
}

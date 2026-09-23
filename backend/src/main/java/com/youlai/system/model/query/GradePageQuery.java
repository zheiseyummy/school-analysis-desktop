package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GradePageQuery extends BasePageQuery {
    @Schema(description="关键字(年级名称)")
    private String keywords;

    @Schema(description = "年级主任ID")
    private Long managerId;
}

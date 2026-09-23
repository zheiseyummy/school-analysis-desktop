package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "考试主体查询对象")
public class ExamBodyPageQuery extends BasePageQuery {
    @Schema(description="关键字(考试名称/考试编码)")
    private String keywords;

    @Schema(description = "考核类型")
    private String examType;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "年级")
    private Long gradeId;

    @Schema(description = "班级")
    private Long clazzId;

}

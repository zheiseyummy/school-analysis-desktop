package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考试分页查询实体
 *
 * @author haoxr
 * @since 2022/6/3
 *
 */
@Data
public class ExamPageQuery extends BasePageQuery {

    @Schema(description="关键字(考试名称/考试编码)")
    private String keywords;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "考核类型")
    private String examType;

    @Schema(description = "学期")
    private Integer semester;
}

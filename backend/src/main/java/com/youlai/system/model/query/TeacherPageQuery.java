package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 职工查询条件
 */
@Data
@Schema(description = "职工查询条件")
public class TeacherPageQuery extends BasePageQuery {
    @Schema(description = "关键字(职工名称/职工编码)")
    private String keywords;


    @Schema(description = "入职年份")
    private Integer year;
}

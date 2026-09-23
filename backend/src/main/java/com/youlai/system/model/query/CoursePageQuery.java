package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程分页查询实体
 *
 * @author haoxr
 * @since 2022/6/3
 *
 */
@Data
public class CoursePageQuery extends BasePageQuery {

    @Schema(description="关键字(课程名称/课程编码)")
    private String keywords;

    @Schema(description = "课程类型")
    private String subjectType;
}

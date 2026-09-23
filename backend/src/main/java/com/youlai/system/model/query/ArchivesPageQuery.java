package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学情档案分页查询实体
 *
 * @author haoxr
 * @since 2022/6/3
 *
 */
@Data
public class ArchivesPageQuery extends BasePageQuery {

    @Schema(description="关键字(学情档案名称/学情档案编码)")
    private String keywords;

    @Schema(description = "年度")
    private Integer year;

    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "教师ID")
    private Long teacherId;
}

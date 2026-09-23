package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 学生查询条件
 */
@Data
public class StudentPageQuery extends BasePageQuery {
    @Schema(description = "关键字(学生名称/学生编码)")
    private String keywords;

    @Schema(description = "入学年份")
    private Integer year;


    @Schema(description = "班级ID")
    private Long clazzId;

    @Schema(description = "学生ID集合列表")
    private List<Long> studentIdList;
}

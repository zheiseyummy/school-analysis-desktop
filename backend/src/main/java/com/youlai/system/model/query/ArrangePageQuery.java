package com.youlai.system.model.query;


import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description ="教学安排数据项分页查询对象")
@Data
public class ArrangePageQuery extends BasePageQuery {

    @Schema(description="班级ID")
    private Long clazzId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "教师ID")
    private Long teacherId;
}

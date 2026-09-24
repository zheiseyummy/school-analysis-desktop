package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_exam_course")
public class SysExamCourse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;
    private Long courseId;
    private Double fullScore;
    private Integer sort;
}

package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "sys_exam_body")
@Data
public class SysExamBody {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 考核安排ID
     */
    private Long examId;

    /**
     * 体检主体ID
     *
     */
    private Long gradeClazzId;

    /**
     * 分组
     */
    private String gOrC;
}

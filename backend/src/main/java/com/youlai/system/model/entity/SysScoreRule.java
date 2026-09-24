package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/** 高中赋分规则配置。method=PENDING 时仅保存配置，不执行计算。 */
@Data
@TableName("sys_score_rule")
public class SysScoreRule extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String method;
    private Double targetFullScore;
    private String configJson;
    private Integer status;
    private String remark;
    private Integer deleted;
}

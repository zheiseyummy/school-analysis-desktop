package com.youlai.system.model.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScoreRuleForm {
    @NotBlank(message = "规则名称不能为空")
    private String name;
    /** PENDING/RAW/PERCENTILE/CUSTOM，PENDING 仅保存配置不计算。 */
    private String method = "PENDING";
    private Double targetFullScore = 100D;
    private String configJson;
    private Integer status = 1;
    private String remark;
}

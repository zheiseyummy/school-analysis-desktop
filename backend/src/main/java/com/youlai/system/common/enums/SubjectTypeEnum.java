package com.youlai.system.common.enums;

import com.youlai.system.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 课程类型枚举
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Schema(enumAsRef = true)
public enum SubjectTypeEnum implements IBaseEnum<Integer> {

    MALE(1, "主科"),
    FEMALE (2, "文科"),
    ALL(3, "理科");

    @Getter
    private Integer value;

    @Getter
    private String label;

    SubjectTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

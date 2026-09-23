package com.youlai.system.common.enums;

import com.youlai.system.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 班级类型枚举
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Schema(enumAsRef = true)
public enum ClazzTypeEnum implements IBaseEnum<Integer> {

    MALE(1, "普通班"),
    FEMALE (2, "文科班"),
    ALL(3, "理科班");

    @Getter
    private Integer value;

    @Getter
    private String label;

    ClazzTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

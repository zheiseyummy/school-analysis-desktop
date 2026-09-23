package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.form.ClazzForm;
import com.youlai.system.model.vo.ClazzPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 班级对象转换器
 */
@Mapper(componentModel = "spring")
public interface ClazzConverter {
    @Mappings({
            @Mapping(target = "clazzTypeLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(java.lang.Integer.valueOf(bo.getClazzType()), com.youlai.system.common.enums.ClazzTypeEnum.class))")
    })
    ClazzPageVO entity2Page(SysClazz bo);
    Page<ClazzPageVO> entity2Page(Page<SysClazz> page);

    SysClazz form2Entity(ClazzForm positionForm);

    ClazzForm entity2Form(SysClazz entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysClazz clazz);


    List<Option> entities2Options(List<SysClazz> clazzs);
}

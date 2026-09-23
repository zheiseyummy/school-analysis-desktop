package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.form.CourseForm;
import com.youlai.system.model.vo.CoursePageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 课程对象转换器
 */
@Mapper(componentModel = "spring")
public interface CourseConverter {
    @Mappings({
            @Mapping(target = "subjectTypeLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(java.lang.Integer.valueOf(bo.getSubjectType()), com.youlai.system.common.enums.SubjectTypeEnum.class))")
    })
    CoursePageVO entity2Page(SysCourse bo);
    Page<CoursePageVO> entity2Page(Page<SysCourse> page);

    SysCourse form2Entity(CourseForm courseForm);

    CourseForm entity2Form(SysCourse entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysCourse course);


    List<Option> entities2Options(List<SysCourse> courses);
}

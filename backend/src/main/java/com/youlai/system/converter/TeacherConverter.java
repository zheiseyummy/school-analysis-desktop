package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysTeacher;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.vo.TeacherExportVO;
import com.youlai.system.model.vo.TeacherImportVO;
import com.youlai.system.model.vo.TeacherPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 职工对象转换器
 */
@Mapper(componentModel = "spring")
public interface TeacherConverter {

    @Mappings({
            @Mapping(target = "sexLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(bo.getSex(), com.youlai.system.common.enums.GenderEnum.class))")
    })
    TeacherPageVO entity2Page(SysTeacher bo);

    Page<TeacherPageVO> entity2Page(Page<SysTeacher> page);

    SysTeacher form2Entity(TeacherForm teacherForm);

    TeacherForm entity2Form(SysTeacher entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysTeacher teacher);


    List<Option> entities2Options(List<SysTeacher> teachers);

    TeacherForm importVo2Entity(TeacherImportVO vo);
    @Mappings({
            @Mapping(target = "sexLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(bo.getSex(), com.youlai.system.common.enums.GenderEnum.class))")
    })
    TeacherExportVO entity2Export(SysTeacher bo);

    List<TeacherExportVO> entity2Export(List<SysTeacher> list);
}

package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.vo.StudentExportVO;
import com.youlai.system.model.vo.StudentImportVO;
import com.youlai.system.model.vo.StudentPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 学生对象转换器
 */
@Mapper(componentModel = "spring")
public interface StudentConverter {

    @Mappings({
            @Mapping(target = "sexLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(bo.getSex(), com.youlai.system.common.enums.GenderEnum.class))")
    })
    StudentPageVO entity2Page(SysStudent bo);

    Page<StudentPageVO> entity2Page(Page<SysStudent> page);

    SysStudent form2Entity(StudentForm studentForm);

    StudentForm entity2Form(SysStudent entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysStudent student);


    List<Option> entities2Options(List<SysStudent> students);

    StudentForm importVo2Entity(StudentImportVO vo);

    @Mappings({
            @Mapping(target = "sexLabel", expression = "java(com.youlai.system.common.base.IBaseEnum.getLabelByValue(bo.getSex(), com.youlai.system.common.enums.GenderEnum.class))")
    })
    StudentExportVO entity2Export(SysStudent bo);

    List<StudentExportVO> entity2Export(List<SysStudent> list);
}

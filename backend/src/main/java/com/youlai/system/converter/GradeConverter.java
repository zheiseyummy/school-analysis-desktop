package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.form.GradeForm;
import com.youlai.system.model.vo.GradePageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 年级对象转换器
 */
@Mapper(componentModel = "spring")
public interface GradeConverter {
    Page<GradePageVO> entity2Page(Page<SysGrade> page);

    SysGrade form2Entity(GradeForm positionForm);

    GradeForm entity2Form(SysGrade entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysGrade grade);


    List<Option> entities2Options(List<SysGrade> grades);
}

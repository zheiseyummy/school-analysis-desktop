package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysExam;
import com.youlai.system.model.form.ExamForm;
import com.youlai.system.model.vo.ExamPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
/**
 *  考试对象转换器
 */
@Mapper(componentModel = "spring")
public interface ExamConverter {


    ExamPageVO entity2VO(SysExam exam);

    Page<ExamPageVO> entity2Page(Page<SysExam> page);

    SysExam form2Entity(ExamForm examForm);

    ExamForm entity2Form(SysExam entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysExam exam);


    List<Option> entities2Options(List<SysExam> exams);
}

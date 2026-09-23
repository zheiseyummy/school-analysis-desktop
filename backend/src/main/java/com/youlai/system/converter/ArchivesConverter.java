package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysArchives;
import com.youlai.system.model.form.ArchivesForm;
import com.youlai.system.model.vo.ArchivesPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 学情档案对象转换器
 */
@Mapper(componentModel = "spring")
public interface ArchivesConverter {
    Page<ArchivesPageVO> entity2Page(Page<SysArchives> page);

    SysArchives form2Entity(ArchivesForm archivesForm);

    ArchivesForm entity2Form(SysArchives entity);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name")
    })
    Option entity2Option(SysArchives archives);


    List<Option> entities2Options(List<SysArchives> archivess);
}

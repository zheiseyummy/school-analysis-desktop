package com.youlai.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.form.ArrangeForm;
import com.youlai.system.model.vo.ArrangePageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

/**
 * 教学安排数据项对象转换器
 *
 * @author haoxr
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface ArrangeConverter {

    Page<ArrangePageVO> entity2Page(Page<SysArrange> page);

    ArrangeForm entity2Form(SysArrange entity);

    @InheritInverseConfiguration(name="entity2Form")
    SysArrange form2Entity(ArrangeForm entity);
}

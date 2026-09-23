package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.bo.ClazzArrangeBO;
import com.youlai.system.model.bo.ClazzCourseBO;
import com.youlai.system.model.entity.SysArrange;
import com.youlai.system.model.query.ArrangePageQuery;
import com.youlai.system.model.query.ClazzCourseQuery;
import com.youlai.system.model.vo.ArrangePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysArrangeMapper extends BaseMapper<SysArrange> {
    Page<ArrangePageVO> getArrangePage(Page<ArrangePageVO> page, ArrangePageQuery queryParams);


    List<ClazzArrangeBO> getAllClazzArrangeList();


    List<ClazzCourseBO> getClazzCourseBOList(@Param("queryParams") ClazzCourseQuery query);
}




